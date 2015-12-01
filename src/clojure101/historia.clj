(ns clojure101.historia
  (:require [datascript.core :as d]
            [clojure.pprint :as pp]))

(comment
  (use '[datascript.core :as d])
  (use '[clojure.pprint :as pp])

  (use 'clojure101.historia :reload-all)

  )

;; (def R (vec (repeatedly 1000000 #(* 1N (rand-int 10000)))))

;; (count R)


;; (d/q '[:find (sum ?n)
;;       :in [?n ...]] R)

;; (reduce + R)

;; (use 'clojure101.security :reload-all)

;; @conn
;; (def V (vec (map first (q '[:find ?n  :in [?n ...]] (map first @conn)))))
;; (map #(transact! conn [[:db.fn/retractEntity %]]) V)
;; (count @conn)
;; ;(add-roles! "admin")
;; ;(add-apps!  "ERP" )
;; ;(add-app-roles! "ERP" "admin")
;; (transact! conn [[:db.fn/retractEntity 12]])


;; (use 'clojure101.security :reload-all)
;; (use 'datascript.core)
;; @conn
;; (count @conn)

;;  (q '[:find [pull ?e [*]]
;;         :in $ [?uid ...]
;;         :where  [?e :user/name ?un]
;;                     [?e :user/id ?uid]]
;;     @conn
;;     ["fgerard" "mgerard" "jperez"])
;;  (q '[:find [pull ?e [:user/name]]
;;         :in $ [?uid ...]
;;         :where [?e :user/id ?uid]]
;;     @conn
;;     ["fgerard" "mgerard" "jperez"])

;; (q '[:find [pull ?x [*]]
;;         :where [?e :app2role/app ?x]]
;;     @conn)

;; (add-apps!  "www" )

;; (pull @conn '[:app2role/_app :app/name] 7)
;; (pull @conn '[{:app2role/app [:app/name]}] 10)
;; (pull @conn
;;       '[:role/name
;;         {:app2role/_role [{:app2role/app
;;                           [:app/name]}]}] 17)
;; (q '[:find ?e
;;        :where [?e :app2role/app ]
;;                    ] @conn)
;; (pull @conn '[* {:app2role/_role [{:app2role/app [:db/id]}]}] 1)
;; (add-app-roles! "ERP" "admin"  )
;; (transact! conn [[:db.fn/retractEntity 25]])

;; (add-roles! "admin" "user" "captur")
;; (add-apps!  "ERP" "CRM" "nomina" "contabilidad")
;; (add-user! "fgerard" "Felipe" "123456")
;; (add-user! "mgerard" "Minoja" "123456")
;; (add-user! "jperez" "Juan" "123456")
;; (get-app-roles "contabilidad")
;; (add-app-roles! "ERP" "admin" "user")
;; (add-usr-app-role! "fgerard" "ERP" "admin")

;; (q '[:find [?celsius ...]
;;        :in [?fahrenheit ...]
;;        :where [(- ?fahrenheit 32) ?f-32]
;;               [(/ ?f-32 1.8) ?celsius]]
;;      [0 32 -40])

;; (q '[:find (sum ?heads) .
;;        :with ?monster
;;        :in [[?monster ?heads]]]
;;      [["Cerberus" 3]
;;       ["Medusa" 1]
;;       ["Cyclops" 1]
;;       ["Chimera" 1]])

;; (q '[:find (sum ?heads) .
;;        :in [[_ ?heads]]]
;;      [["Cerberus" 1]
;;       ["Medusa" 1]
;;       ["Cyclops" 1]
;;       ["Chimera" 2]])

;; (q '[:find (distinct ?v) .
;;        :in [?v ...]]
;;      [1 1 2 2 2 3])

(def data [["Felipe" "Gerard" 54 10000]
           ["Minoja" "Valdes" 51 8000]
           ["Juan" "Perez" 25 1500]
           ["Pedro" "Martinez" 30 2500]
           ["Roberto" "Lopez" 33 2500]
           ["Carlos" "Gomez" 25 2500]
           ["Maria" "Sanchez" 28 4300]])
;
(d/q '[:find ?nom ?pat ?edad ?limite
       :in $
       :where [?nom ?pat ?edad ?limite]]
     data)

(d/q '[:find ?nom ?pat ?edad ?limite
       :in $
       :where [?nom ?pat ?edad ?limite]
              [(= ?edad 25)]]
     data)

; funciones solas sin binding en el where son filtros
(d/q '[:find ?nom ?pat ?edad ?limite
       :in $
       :where [?nom ?pat ?edad ?limite]
       [(> ?limite 7000)]]
     data)

; como parametros
(d/q '[:find ?nom ?pat ?edad ?limite
       :in $ ?minimo
       :where [?nom ?pat ?edad ?limite]
              [(> ?limite ?minimo)]]
     data 7000)

(defn con-limite-superior [importe]
  (d/q '[:find ?nom ?pat ?edad ?limite
         :in $ ?minimo
         :where [?nom ?pat ?edad ?limite]
         [(> ?limite ?minimo)]]
       data importe))

(con-limite-superior 4000)

;(def data2 [[1 :nombre "Felipe"]
;            [1 :paterno "Gerard"]
;            [1 :edad 54]
;            [1 :limite 10000]
;            [2 :nombre "Minoja"]
;            [2 :paterno "Valdes"]
;            [2 :edad 51]
;            [2 :limite ]])

(defn add-entity [result [nom pat edad limite id]]
  (concat result [[(- -1000 id) :cte/nombre nom]
                  [(- -1000 id) :cte/paterno pat]
                  [(- -1000 id) :cte/edad edad]
                  [(- -1000 id) :cte/limite limite]]))

(pp/pprint (reduce add-entity [] (map-indexed #(conj %2 %1) data)))

(def data2 (atom (reduce add-entity [] (map-indexed #(conj %2 %1) data))))

(pp/pprint @data2)

;Nueva relación
(swap! data2 #(concat % [[-1000 :cte/esposa -1001]
                         [-1001 :cte/esposo -1000]
                         [-1003 :cte/esposa -1006]
                         [-1006 :cte/esposo -1003]]))

;Lista los casados
(d/q '[:find ?nom ?edad
       :in $
       :where [?p0 :cte/esposa _]
              [?p0 :cte/nombre ?nom]
              [?p0 :cte/edad ?edad]]
     @data2)
;
;;Lista las casadas
(d/q '[:find ?nom ?edad
       :in $
       :where [?p0 :cte/esposo _]
       [?p0 :cte/nombre ?nom]
       [?p0 :cte/edad ?edad]]
     @data2)
;
;
;;Limite por pareja OJO funciones con un binding UNIFICA !!
(d/q '[:find ?limite
       :in $
       :where [?p0 :cte/esposa ?p1]
              [?p1 :cte/limite ?lim1]
              [?p0 :cte/limite ?lim0]
              [(+ ?lim0 ?lim1) ?limite]]
     @data2)

;; notacion pull  NO JALA SE NECESITA MÁS que una estructura!!!
(d/q '[:find (pull ?e [*])
       :in $
       :where [?e :cte/nombre "Felipe"]]
     @data2)
;
;;; ==================== ahora definiendolo como base de datos
;
(def conn1 (d/create-conn))

(d/transact! conn1 (mapv #(vec (concat [:db/add] %)) @data2))

(pp/pprint @conn1)

(d/pull @conn1 '[*] 1)

(d/q '[:find (pull ?e [*])
       :in $
       :where [?e :cte/edad 25]]
     @conn1)

(def data3 (atom (reduce add-entity [] (map-indexed #(conj %2 %1) data))))

(pp/pprint @data3)

(def conn (d/create-conn {:cte/esposa {:db/valueType :db.type/ref}
                          :cte/nombre {:db/index true}}))

(d/transact! conn (mapv #(vec (concat [:db/add] %)) @data3))

(defn get-id [nombre]
  (d/q '[:find ?e .
         :in $ ?nombre
         :where [?e :cte/nombre ?nombre]]
       @conn nombre))

(d/pull @conn '[*] 1)

(let [el (get-id "Felipe")
      ella (get-id "Minoja")]
  (d/transact! conn [[:db/add el :cte/esposa ella]]))

(d/pull @conn '[*] 1)

;Pareja
(d/pull @conn '[:cte/nombre {:cte/esposa 1}] 1)
(d/pull @conn '[:cte/nombre :cte/limite {:cte/esposa 1}] 1)
(d/pull @conn '[:cte/nombre :cte/limite {:cte/esposa ...}] 1)

;Navegacion de reversa!!
(d/pull @conn '[:cte/nombre {:cte/_esposa ...}] 2)
;
;AUN  NO JALA EN DATASCRIPT pero sí en Datomic
(d/q '[:find ?entity ?name ?tx ?score
       :in $ ?search
       :where [(fulltext $ :cte/nombre ?search) [[?entity ?name ?tx ?score]]]]
     @conn "Feli")
;

(d/q '[:find ?nombre
       :in $
       :where
         [_ :cte/nombre ?nom]
         [(str "Mr. " ?nom) ?nombre]]
     @conn)

;; Uso de los indices X entidad
(d/datoms @conn :aevt :cte/nombre)

;; Uso de los indices X valor del atributo (debe tener indice!! VER SCHEMA)
(d/datoms @conn :avet :cte/nombre)

(pp/pprint (map #(d/pull @conn '[*] (first %)) (d/datoms @conn :avet :cte/nombre)))
