(ns rsa.core)

;; Helper functions used to retrieve a list of many primes to select from.
(defn prime? [n]
         (.isProbablePrime (BigInteger/valueOf n) 5)) ;; 5 is the certainty value.
(def many-primes (take 50 
                       (filter prime?
                             (range 1 Integer/MAX_VALUE))))
(defn get-random-prime []
  (nth many-primes (rand-int (count many-primes))))

;; Define Alice's keys.
(def alice-p (get-random-prime))
(def alice-q (get-random-prime))
(def alice-n (* alice-p alice-q))
(def alice-e (get-random-prime))
(def alice-d (first 
               (filter 
                 #(= (mod (* % alice-e) (* (- alice-p 1) (- alice-q 1)))
                    1) (range 1000000)))) ; Decryption key used by Bob.

;; Help functions to convert numeric to alpha values and vice-versa.
(defn char-to-int [msg]
  (map #(int %) (seq msg)))

(defn int-to-char [msg]
  (map #(char %) msg))

;; All the magic!
(defn encrypt-text [msg-sequence e n]
  (map #(mod (apply * (repeat e (bigint %))) n) msg-sequence)) ;; Enforce bigint, the values should get large!

(defn decrypt-text [msg-sequence d n]
  (map #(mod (apply * (repeat d (bigint %))) n) msg-sequence))

;; Entry point.
(defn main[]
  (println "\nAlice's private keys:" "\np -> " alice-p "\nq -> " alice-q "\ne -> " alice-e)
  (println "\nAlice's public keys:" "\nd -> " alice-d "\nn -> " alice-n)
  (print "\nEnter some text: ")
  (let [input (read-line)
        int-seq (char-to-int input)
        encrypted-msg (encrypt-text int-seq alice-e alice-n)
        decrypted-msg (decrypt-text encrypted-msg alice-d alice-n)]
    (println input)
    (println "\nAlice sends the encrypted message (numeric): " encrypted-msg)
    (println "Alice sends the encrypted message (alpha): " (apply str (int-to-char encrypted-msg)))
    (println "\nBob decrypts Alice's message (numeric): " decrypted-msg)
    (println "Bob decrypts Alice's message (alhpa): " (apply str (int-to-char decrypted-msg)))))