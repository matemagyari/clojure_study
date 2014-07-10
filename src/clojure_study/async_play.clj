(ns clojure-study.async-play
  (:require [clojure.core.async :refer [go go-loop chan close! <! <!! >! >!!
                                        timeout filter< put! take! pub sub unsub unsub-all
                                        thread alts! alts!!]]
            [clojure-study.assertion :refer [assert-equals]]))


(let [c (chan 10)]
  (>!! c "hello")
  (assert (= "hello" (<!! c)))
  (close! c))

;================== GO ====
(let [c (chan)]
  (go (>! c "hello"))
  (assert (= "hello" (<!! (go (<! c)))))
  (close! c))

;======================
(let [store (atom nil)
      c (chan)]
  (go (while true
        (if-let [msg (<! c)]
          (reset! store msg))))
  (>!! c "hi")
  (assert-equals "hi" @store)
  (>!! c "ho")
  (<!! (timeout 10))
  (assert-equals "ho" @store)
  (close! c))

;================== GO-LOOP ====
(let [store (atom nil)
      c (chan)]
  (go-loop []
    (if-let [msg (<! c)]
      (reset! store msg))
    (recur))
  (>!! c "hi")
  (assert-equals "hi" @store)
  (<!! (timeout 10))
  (close! c))

;============ GO - a bit more complex example ======
(let [store (atom [])
      c (chan 10)]
  (go
    (while true (when-let [msg (<! c)]
                  (swap! store conj msg))))
  (go (doseq [x (range 4)]
        (>! c x)))
  (<!! (timeout 10))
  (assert-equals [0 1 2 3] @store))

;============== FILTER< ======
(let [xstore (atom 0)
      store (atom 0)
      num-chan (chan)
      lt-5-chan (filter< #(> 5 %) num-chan)]
  (go
    (while true
      (if-let [msg (<! lt-5-chan)]
        (reset! xstore msg))))
  (go
    (while true
      (if-let [msg (<! num-chan)]
        (reset! store msg))))
  (>!! num-chan 4)
  (>!! num-chan 6)
  (<!! (timeout 100))
  (assert-equals 4 @xstore)
  (assert-equals 6 @store)
  (close! num-chan))

;==================== PUT and TAKE =====
(let [c (chan)
      listener (fn [m] (println m))]
  (take! c listener)
  (put! c "baba")
  (put! c "mama")
  (close! c))

;==================== PUBLISH AND SUBSCRIBE =====
(let [in (chan)
      out-cat (chan)
      out-dog (chan)
      store-dog (atom nil)
      store-cat (atom nil)
      publication (pub in :type)
      take-and-store! (fn [channel store]
                        (go-loop []
                          (if-let [msg (<! channel)]
                            (reset! store msg))
                          (recur)))]
  (sub publication :cat out-cat)
  (sub publication :dog out-dog)
  (take-and-store! out-cat store-cat)
  (take-and-store! out-dog store-dog)
  (>!! in {:name :Mia :type :cat})
  (>!! in {:name :Vau :type :dog})
  (<!! (timeout 10))
  (assert-equals :Mia (:name @store-cat))
  (assert-equals :Vau (:name @store-dog))
  (unsub-all publication)
  (close! in))

;==================== PUBLISH AND SUBSCRIBE 2=====
(let [num-chan (chan)
      chan-even (chan)
      chan-odd-1 (chan)
      chan-odd-2 (chan)
      store-even (atom [])
      store-odd (atom [])
      store-odd-2 (atom [])
      topic-fn #(if (even? %) :even :odd)
      publication (pub num-chan topic-fn)
      take-and-store! (fn [channel store]
                        (go-loop []
                          (if-let [msg (<! channel)]
                            (swap! store conj msg))
                          (recur)))]
  (sub publication :even chan-even)
  (sub publication :odd chan-odd-1)
  (sub publication :odd chan-odd-2)
  (take-and-store! chan-even store-even)
  (take-and-store! chan-odd-1 store-odd)
  (take-and-store! chan-odd-2 store-odd-2)
  (doseq [x (range 10)] (>!! num-chan x))
  (<!! (timeout 10))
  (unsub-all publication)
  (close! num-chan)
  (assert-equals [0 2 4 6 8] @store-even)
  (assert-equals [1 3 5 7 9] @store-odd)
  (assert-equals [1 3 5 7 9] @store-odd-2))

;==================== THREAD =====
(let [ct (thread (+ 21 21))
      cg (go (+ 21 21))]
  (assert-equals 42 (<!! ct))
  (assert-equals 42 (<!! cg)))
;==================== ALT! =====
(let [fast-chan (chan)
      slow-chan (chan)]
  (go (Thread/sleep 10)
    (>! slow-chan 0))
  (go (>! fast-chan 1))
  (let [ [v c] (alts!! [fast-chan slow-chan])]
    (assert-equals 1 v)
    (assert-equals fast-chan c)))
;==================== Lot of channels with ALT! =====
(let [n 1000
      channels (repeatedly n chan)
      begin (System/currentTimeMillis)]
  (doseq [c channels] (go (>! c "hi")))
  (dotimes [i n]
    (let [ [v c] (alts!! channels)]
      (assert-equals "hi" v)))
  (println "Read" n "messages in" (- (System/currentTimeMillis) begin) "ms"))


