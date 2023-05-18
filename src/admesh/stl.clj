(ns admesh.stl
  "Create stl structs.

  All the layout files are generated by:

  ```sh
  mdkir admesh-debug && cd ademsh-debug
  cmake ..
  make src/admesh.c.i
  clang -v ./CMakeFiles/admesh.dir/src/admesh.c.i -Xclang -fdump-record-layouts
  ```
  "
  (:require
   [clojure.java.io :as io]
   [clojure.string :as s]
   [tech.v3.datatype.ffi.clang :as ffi-clang]
   [tech.v3.datatype.struct :as dt-struct]))

(defn fail-line-parser [v]
  (let [t (first (s/split (s/trim v) #"\s+"))]
    (if (s/starts-with? t "stl")
      (keyword (s/lower-case (s/replace t "_" "-")))
      nil)))

(def stl-normal
  (dt-struct/define-datatype! :stl-normal [{:name :x :datatype :float32}
                                           {:name :y :datatype :float32}
                                           {:name :z :datatype :float32}]))

(def stl-vertex*
  (delay (ffi-clang/defstruct-from-layout
           :stl-vertex (slurp (io/resource "admesh/layout_stl_vertex.txt")))))

(def stl-edge*
  (delay
    @stl-vertex*
    (ffi-clang/defstruct-from-layout
      :stl-edge (slurp (io/resource "admesh/layout_stl_edge.txt"))
      {:failed-line-parser fail-line-parser})))

(def stl-facet*
  (delay
    @stl-vertex*
    (ffi-clang/defstruct-from-layout
      :stl-facet (slurp (io/resource "admesh/layout_stl_facet.txt"))
      {:failed-line-parser fail-line-parser})))

(def stl-hash-edge*
  (delay (ffi-clang/defstruct-from-layout
           :stl-hash-edge (slurp (io/resource "admesh/layout_stl_hash_edge.txt")))))

(def stl-neighbors*
  (delay (ffi-clang/defstruct-from-layout
           :stl-neighbors (slurp (io/resource "admesh/layout_stl_neighbors.txt")))))

(def stl-v-indicies-struct*
  (delay (ffi-clang/defstruct-from-layout
           :stl-v-indicies-struct (slurp (io/resource "admesh/layout_stl_v_indices_struct.txt")))))

(def stl-stats*
  (delay
    @stl-vertex*
    (ffi-clang/defstruct-from-layout
      :stl-stats (slurp (io/resource "admesh/layout_stl_stats.txt"))
      {:failed-line-parser fail-line-parser})))

(def stl-file*
  (delay
    @stl-stats*
    (ffi-clang/defstruct-from-layout
      :stl-file (slurp (io/resource "admesh/layout_stl_file.txt"))
      {:failed-line-parser fail-line-parser})))

;; typedef enum {binary, ascii, inmemory} stl_type;

(comment
  @stl-vertex*
  @stl-edge*
  @stl-facet*
  @stl-hash-edge*
  @stl-neighbors*
  @stl-v-indicies-struct*
  @stl-stats*
  @stl-file*)