(ns clojure101.security
  (:require [datascript.core :as d]))


(def d-schema
  {;:user/roles {:db/cardinality :db.cardinality/many
    ;                  :db/valueType :db.type/ref}
   ;:app/roles {:db/cardinality :db.cardinality/many
   ;                   :db/valueType :db.type/ref}
   :role/name {:db/unique :db.unique/value}
   :app/name {:db/unique :db.unique/value}
   :user/id {:db/unique :db.unique/value}
   :app2role/app {:db/valueType :db.type/ref}
   :app2role/role {:db/valueType :db.type/ref}
   :usr2role/usr {:db/valueType :db.type/ref}
   :usr2role/role {:db/valueType :db.type/ref}


   })

(def conn (d/create-conn d-schema))

(defn get-id [attr value]
   (ffirst (d/q '[:find ?e :in $ ?attr ?value :where [?e ?attr ?value]] @conn attr value)))

(defn get-entity [attr value]
  (d/touch (d/entity @conn (get-id attr value))))

(defn add! [attr & names]
  (d/transact! conn
               (mapv (fn [temp-id name]
                            {:db/id (- -1 temp-id) attr name})
                     (range) names)))

(defn add-apps! [& app-names]
  (apply add! :app/name app-names))

(defn add-roles! [& role-names]
  (apply add! :role/name role-names))

(defn add-user! [userid name password]
  (d/transact! conn
               [{:db/id -1
                  :user/id userid
                  :user/name name
                  :user/passwd password}]))

(defn remove-user! [userid]
  (if-let [uid (get-id :user/id userid)]
    (d/transact! conn [[:db.fn/retractEntity uid]])))

(defn- get-rel-entity [db link2master-attr master-id link2slave-attr slave-id]
  (let [e (ffirst (d/q '[:find ?e
                               :in $ ?link2master-attr ?master-id ?link2slave-attr ?slave-id
                               :where [?e ?link2master-attr ?master-id]
                                           [?e  ?link2slave-attr ?slave-id]]
                       @conn
                       link2master-attr master-id link2slave-attr slave-id))]
    (not e)))

;                             :app/name "ERP"            :role/name   :app2role/app    :app2role/role   roles
(defn- intern-relaciona [db master-attr master-value slave-attr    link2master-attr link2slave-attr    slave-ids]
  (if-let [main-id (ffirst (d/q '[:find ?e
                                          :in $ ?master-attr ?master-value
                                          :where [?e ?master-attr ?master-value]]
                                   db master-attr master-value))]
    (let [slave-id-set (into #{} slave-ids)
            rls (d/q '[:find ?e
                           :in $ ?attr
                           :where [?e ?attr]]
                     (d/filter db (fn [db dtm]
                                        (and (= slave-attr (.a dtm)) (slave-id-set (.v dtm)))))
                     slave-attr)
            rls (map first rls)]
      (mapv (fn [tmp-id r-id]
                  {:db/id (- -1 tmp-id)
                    link2slave-attr r-id
                    link2master-attr main-id})
            (range) (filter (partial get-rel-entity db link2master-attr main-id link2slave-attr)  rls)))))

(defn rel-app-role [db app-name roles]
  (intern-relaciona db :app/name app-name :role/name :app2role/app :app2role/role roles))

(defn rel-usr-role [db usr-id roles]
  (intern-relaciona db :user/id usr-id :role/name :usr2role/usr :usr2role/role roles))

(defn add-app-roles!
  "Funcion que crea relaciones entre app y role regresa el numero de nievas relaciones creadas"
  [app-name & roles]
  (let [txs (d/transact! conn [[:db.fn/call rel-app-role app-name roles]])
          new-links (/ (count (seq (:tx-data txs))) 2)]
    new-links))

;(defn add-usr-roles!
;  "Funcion que crea relaciones entre app y role regresa el numero de nuevas relaciones creadas"
;  [usr-id & roles]
;  (let [txs (d/transact! conn [[:db.fn/call rel-usr-role usr-id roles]])
;          new-links (/ (count (seq (:tx-data txs))) 2)]
;    new-links))

(defn- get-app2role [db app-name role-name]
  (ffirst (d/q '[:find ?e-app2role
                     :in $ ?app-name ?role-name
                     :where [?e-app :app/name ?app-name]
                                 [?e-role :role/name ?role-name]

                 [?e-app2role :app2role/app ?e-app]
                                 [?e-app2role :app2role/role ?e-role]] db app-name role-name)))

(defn has-role? [db user-id app-name role-name]
  (ffirst
   (d/q '[:find ?u
            :in $ ?userid ?app-name ?role-name
            :where [?u :user/id ?userid]
                        [?a :app/name ?app-name]
                        [?r :role/name ?role-name]
                        [?u->r :usr2approle/usr ?u]
                        [?a->r :app2role/role ?r]
                        [?u->r :usr2approle/role ?a->r]
                        [?a->r :app2role/app ?a]]
       db user-id app-name role-name)))

(defn- connect-user2app-role [db userid app-name role-name]
  (if-not (has-role? db userid app-name role-name)
    (if-let [eu (get-id :user/id userid)]
      (if-let [e-app2role (get-app2role db app-name role-name)]
        [{:db/id -1 :usr2approle/usr eu :usr2approle/role e-app2role}]))))


(defn add-usr-app-role! [userid app-name role-name]
  (let [txs (d/transact! conn [[:db.fn/call connect-user2app-role userid app-name role-name]])
          txs (/ (count (:tx-data txs)) 2)]
    txs))

;(d/filter @conn (fn [db dtm] (#{"admin" "captur"} (.v dtm))))

(defn get-app-roles "Funcion que regresa la lista de roles asociados con un app"
  [app-name]
  (d/q '[:find ?r
            :in $ ?app-name
            :where
              [?ea :app/name ?app-name]
              [?app->role :app2role/app ?ea]
              [?app->role :app2role/role ?er]
              [?er :role/name ?r]
         ]
       @conn
       app-name))

(defn get-usr-roles "Funcion que regresa la lista de roles asociados con un user"
  [usr-id]
  (d/q '[:find ?r
            :in $ ?usr-id
            :where
              [?e :user/id ?usr-id]
              [?usr->role :usr2role/usr ?e]
              [?usr->role :usr2role/role ?er]
              [?er :role/name ?r]
         ]
       @conn
       usr-id))



(add-roles! "admin" "user" "captur")
(add-apps!  "ERP" "CRM" "nomina" "contabilidad")
(add-user! "fgerard" "Felipe" "123456")
(add-user! "mgerard" "Minoja" "123456")
(get-app-roles "contabilidad")
(add-app-roles! "contabilidad" "admin" "user")

;(add-usr-roles! "fgerard" "contabilidad" "admin")
; para lograr lo anterior:
;1. encontrar [?e-usr :user/id "fgerard"]
;2.                [?e-app :app/name "contabilidad"]
;3.                [?e-app-role :app2role/app ?e-app]
;4.                [?e-app-role :app2role/role ?e-role]
;5.                [?e-role :role/name "admin"]
;6. insertar {:db/id -1 :usr2approle/usr ?e-usr :usr2approle/role ?e-app-role}
;
;para saber si un usuario tiene un rol para una app ej: fgerard contabilidad admin
;    [:find ?u
;     :where [?u :user/id "fgerard"]
;                 [?a :app/name "contabilidad"]
;                 [?r :role/name "admin"]
;                 [?u->r :usr2approle/usr ?u]
;                 [?a->r :app2role/role ?r]
;                 [?u->r :usr2approle/role ?a->r]
;                 [?a->r :app2role/app ?a]]
;(d/transact! conn [{:db/id -1 :usr2approle/usr 8 :usr2approle/role 10}])

;(add-usr-roles! "mgerard" "user" "captur")
;(get-usr-roles "mgerard")


;(d/touch (d/entity @conn 8))
;(defn add-app! [app-name & roles]
;  (d/transact! conn  (concat [{:db/id -1
;                                              :app/name app-name}]
;                                           (mapv (fn [
;
;                                :app/roles  (mapv
;                                                  (fn [role]
;                                                    {:role/name role}) roles)}]))

;(defn set-app-roles! [app-name & roles]
;  (if-let [app-id (ffirst (d/q '[:find ?e :in $ ?app-name :where [?e :app/name ?app-name]] @conn app-name))]
;    (d/transact! conn (concat [{:db/id app-id
;                                               :app/roles (range -1 (- 0 (inc (count roles))) -1)}]
;                              (mapv (fn [tmp-id role]
;                                            {:db/id tmp-id
;                                              :role/name role}) (range -1 (- 0 (inc (count roles))) -1) roles)))))


;(set-app-roles! "nomina" "admin-n")
;(ffirst (d/q '[:find ?e :in $ ?app-name :where [?e :app/name ?app-name]] @conn "nominax"))
;(add-app! "contabilidad" "admin" "user" "capturista")
;(comment add-app! "nomina")

;(d/q '[:find ?e ?n ?r :where [?e :app/name ?n]
;                                          [?e :app/roles ?rid]
;                                          [?rid :role/name ?r]] @conn)
;@conn
;(d/entity @conn 1)
;(d/transact! conn [[:db.fn/retractEntity 6]])
;(d/transact! conn [
;                   {:db/id -1
;                     :app/name "conta"
;                    :app/roles [-2 -3]}
;                   {:db/id -2
;                    :role/name "admin"}
;                   {:db/id -3
;                    :role/name "user"}
;                   ])

;(defn add-user! [userid user-name]
;  (d/transact! conn [{:db/id -1
;                                :userid userid
;                                :user-name user-name}]))

;(add-user! "fgerard" "Felipe")
;(add-user! "mgerard" "Minoja")





;(defn add-app-role! [app-name role-name]
; (d/transact! conn [{:db/id -1
;                                :app-name userid
;                                :user-name user-name}]))

