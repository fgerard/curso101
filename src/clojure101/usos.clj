(ns clojure101.usos
  (:require
   [clojure.pprint :as pp]
   [clojure.string :as s]
   [clojure.core.match :as M]))

(def v (vec (range 0 100)))

(def r (vec (repeatedly 100 #(rand-int 1000))))

(group-by identity r)

(def r (vec (repeatedly 100  #(rand-int 10))))

(into {}
   (map (fn [[n lst]]
              [n (count lst)])
        (group-by identity r)))

(frequencies r)

(filter odd? r)

(filter even? r)

(count (filter odd? r))
(count (filter even? r))

(def nums [:cero :uno :dos :tres :cuatro :cinco :seis :siete :ocho :nueve])

(def num-map (reduce (fn [R [k v]]
                       (assoc R k v)) {} (map (fn [k v] [k v]) nums (range))))

(case (+ 2 2)
 (1 3) :UNO-TRES
 (2 4) :DOS-CUATRO)

(def r (vec (repeatedly 1000  #(rand-int 10))))
(count r)
(dedupe r)
(count (dedupe r))

(def DB (atom {}))

(defmulti process first)

(defmethod process :alta [[cmd id]]
  (println "damos de alta " id)
  (swap! DB assoc id {:saldo 0}))

(defmethod process :baja [[cmd id]]
  (println "damos de baja " id)
  (swap! DB dissoc id))

(defmethod process :abona [[cmd id imp]]
  (println "abonamos a " id " $" imp)
  (swap! DB update-in [id :saldo] #(+ % imp)))

(defmethod process :carga [[cmd id imp]]
  (println "cargamos a " id " $" imp)
  (swap! DB update-in [id :saldo] #(- % imp)))

(let [eventos [[:alta "Felipe"]
               [:alta "Juan"]
               [:abona "Felipe" 100]
               [:alta "Pedro"]
               [:carga "Felipe" 10]
               [:abona "Pedro" 7]
               [:baja "Juan"]
               [:alta "Luis"]]]
  (doseq [evt eventos]
    (println "Procesando evento:" evt)
    (process evt)))
