(ns twitter-reader.conn-repository
  "Repository of connections")

;; Storing connections and statistics as a map of ws-channel -> stats
(def ^:private conns (atom {}))

(defn reset-conn! [conn words]
  (swap! conns assoc conn {:tweets []
                           :search-words words}))

(defn remove-conn! [conn] (swap! conns dissoc conn))

(defn update-tweets! [conn tweets] (swap! conns assoc-in [conn :tweets] tweets))

(defn get-conn [ws-channel] (get @conns ws-channel))



