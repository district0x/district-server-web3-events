(ns tests.foo-test
  (:require [clojure.test :refer [deftest is testing]]
            [mount.core :as mount]
            [cljs.core.async :refer [<! timeout]]
            [district.server.web3 :refer [web3]]
            [cljs-web3-next.eth :as web3-eth]
            [cljs-web3-next.utils :as web3-utils]
            [district.server.smart-contracts]
            [district.server.web3-events :as web3-events])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(defn handle-some-event []
  (println "Handling some event"))

(defn handle-some-other-event []
  (println "Handling some other event"))

(defn on-registrar-transfer [err {{:keys [:from :to :id] :as args} :args}]
  (println 12312312343434345345345)
  #_(safe-go
      (when (db/offering-exists? to)
        (let [offering (<! (offering/get-offering to))
              node-owner? (<! (node-owner? to offering))]
          (log/info info-text {:args args} ::on-registrar-new-owner)
          (db/set-offering-node-owner?! {:offering/address to
                                         :offering/node-owner? node-owner?})))))

(defn start [opts]
  ;(web3-events/register-callback! :my-contract/some-event handle-some-event ::some-event)
  ;(web3-events/register-callback! :my-contract/some-other-event handle-some-other-event ::some-other-event)
  (web3-events/register-callback! :registrar/transfer on-registrar-transfer ::registrar-transfer)
  opts)

(defn stop []
  (web3-events/unregister-callbacks! [::some-event ::some-other-event ::registrar-transfer]))

;:web3-events {:events {:my-contract/some-event [:my-contract :SomeEvent]
;                       :my-contract/some-other-event [:my-contract :SomeOtherEvent]}

(mount/defstate my-module
                :start (start (mount/args))
                :stop (stop))

(def url "wss://mainnet.infura.io/ws/v3/fd0074468fd64e36b495c846a26a3f9d")
(def from-block 12958350)

(def smart-contracts
  {
   ;:ens {:name "ENSRegistry" :address "0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e"}
   ;:eth-registrar {:name "NameBazaarDevRegistrar" :address "0x57f1887a8BF19b14fC0dF6Fd9B2acc9Af147eA85"}
   ;:offering-registry {:name "OfferingRegistry" :address "0x5B5B2B386a84546Fa75E9472e3096C26576B6d24"}
   ;:buy-now-offering {:name "BuyNowOffering" :address "0x45141Ef2E09412eAd94793617507A0F487249fB0"}
   ;:buy-now-offering-factory {:name "BuyNowOfferingFactory" :address "0xF9Bc1b1bBC9C91864e68D861038282ebdcf2449A"}
   ;:auction-offering {:name "AuctionOffering" :address "0x9cf308c1d04AeD7eEA994f029cA2E39517C6372f"}
   ;:auction-offering-factory {:name "AuctionOfferingFactory" :address "0xF787200012f8d3EA6Ee686577aA73Ed4a3e3ee0C"}
   ;:district0x-emails {:name "District0xEmails" :address "0x02441d5dd828CccB3F81ae702eeEccc2142a192e"}
   ;:reverse-name-resolver {:name "NamebazaarDevNameResolver" :address "0xA2C122BE93b0074270ebeE7f6b7292C7deB45047"}
   ;:public-resolver {:name "NamebazaarDevPublicResolver" :address "0x4976fb03C32e5B8cfe2b6cCB31c09Ba78EBaBa41"}
   :reverse-registrar {:name "NamebazaarDevReverseRegistrar" :address "0x084b1c3C81545d370f3634392De611CaaBFf8148"}
   })

(mount/start
  (mount/with-args
    {:web3 {:url url}
     :web3-events {:events {:ens/new-owner [:ens :NewOwner]
                            :ens/transfer [:ens :Transfer]
                            :offering-registry/offering-added [:offering-registry :on-offering-added]
                            :offering-registry/offering-changed [:offering-registry :on-offering-changed]
                            :registrar/transfer [:eth-registrar :Transfer]}
                   :step-size 10
                   :from-block from-block}
     :smart-contracts {:contracts-build-path "./test/tests/contracts-build/"
                       :contracts-var #'smart-contracts}
     :logging {:level :info
               :console? true}}))

(deftest mainnet-test
  (testing "manual test"
    (go (is (true? (<! (web3-eth/is-listening? @web3)))
            "connected?"))))

(js/setTimeout mount/stop (* 5 1000))