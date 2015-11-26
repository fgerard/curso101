(ns clojure101.historia
  (:require [datascript.core :as d]))

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

