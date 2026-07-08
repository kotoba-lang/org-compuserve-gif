(ns gif.lzw-test
  "LZW validated against REAL Pillow-encoded fixtures with ground-truth
   pixel indices. Bit-exact: LSB, non-early, including a 96x40 interlaced
   image (4-pass de-ordering)."
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [gif.core :as gif]))

(defn- rd [p] (mapv #(bit-and (int %) 0xff)
                    (with-open [in (io/input-stream (io/resource p))] (.readAllBytes in))))
(defn- expected [p] (edn/read-string (slurp (io/resource p))))

(deftest gif-lzw-pixels
  (testing "small 4x2 indexed LZW vs Pillow ground truth"
    (is (= (get-in (expected "gif/fixtures/expected.edn") [:gif-idx :indices])
           (gif/first-frame-indices (rd "gif/fixtures/lzw_idx.gif")))))
  (testing "96x40 interlaced indexed LZW (bit-exact, 4-pass de-ordering)"
    (let [exp (get-in (expected "gif/fixtures/expected_big.edn") [:gif :indices])]
      (is (= 3840 (count exp)))
      (is (= exp (gif/first-frame-indices (rd "gif/fixtures/lzw_big.gif")))))))
