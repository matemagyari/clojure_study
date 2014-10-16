(ns ^{:author mate.magyari
      :doc "Swing UI. "}
  marsrovers.display)

;; -----------------  private functions ------------------------

(import '(javax.swing JFrame JPanel)
  '(java.awt Color Graphics Dimension)
  '(java.awt.image BufferedImage))

(defn- fmap [f coll] (doall (map f coll)))

(defn- render-cell! [#^Graphics g cell dim-scale]
  (let [[state x y] cell
        x (inc (* x (dim-scale 0)))
        y (inc (* y (dim-scale 1)))]
    (doto g
      (.setColor (if (= state :dead) Color/GRAY Color/WHITE))
      (.fillRect x y (dec (dim-scale 0)) (dec (dim-scale 1))))))

(defn- render! [g img bg board dim-scale dim-screen]
  (.setColor bg Color/BLACK)
  (.fillRect bg 0 0 (dim-screen 0) (dim-screen 1))
  (fmap (fn [col]
          (fmap #(when (not= :empty (% 0))
                   (render-cell! bg % dim-scale)) col)) board)
  (.drawImage g img 0 0 nil))

(defn- create-panel [board dim-board dim-screen]
  (let [dim-scale (vec (map / dim-screen dim-board))
        img (BufferedImage. (dim-screen 0) (dim-screen 1) (BufferedImage/TYPE_INT_ARGB))
        bg (.getGraphics img)
        panel
        (doto (proxy [JPanel] []
                (paint [g] (render! g img bg @board dim-scale dim-screen)))
          (.setPreferredSize (new Dimension
                               (* (dim-scale 0) (dim-board 0))
                               (* (dim-scale 1) (dim-board 1)))))]
    (doto (new JFrame) (.add panel) .pack .show (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE))
    panel))

;; -----------------  public functions ------------------------

(defn repaint-fn
  "Returns a function that will be called with one argument containing the positions of the rovers
   and will refresh the Swing panel"
  [dim-board dim-screen]
  (let [board-atom (atom [])
        panel (create-panel board-atom dim-board dim-screen)]
    (fn [board] (do
                  (reset! board-atom board)
                  (.repaint panel)))))
