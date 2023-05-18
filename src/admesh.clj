(ns admesh
  (:require
   [admesh.stl]
   [tech.v3.datatype.ffi :as dt-ffi]
   [tech.v3.datatype.native-buffer :as native-buffer]))

(def fn-defs
  {:stl_open             {:rettype :void
                          :argtypes [['stl :pointer]
                                     ['file :string]]}
   :stl_calculate_volume {:rettype :void
                          :argtypes [['stl :pointer]]}})

(defonce lib (dt-ffi/library-singleton #'fn-defs))
(dt-ffi/library-singleton-reset! lib)

(dt-ffi/define-library-functions
  fn-defs
  (fn [fn-name] (dt-ffi/library-singleton-find-fn lib fn-name))
  nil)

(comment
  (->> "/tmp/demo/libadmesh.dylib" (dt-ffi/library-singleton-set! lib))

  (def stl-pointer (dt-ffi/make-ptr :pointer 0))

  ;; open and calculate.
  (do (stl_open stl-pointer "/tmp/demo/demo.stl")
      (stl_calculate_volume stl-pointer))

  ;; direct access via address
  (def sp (dt-ffi/->pointer stl-pointer))
  (.getFloat (native-buffer/unsafe) (+ (.address sp) 208))

  ;; access via struct
  (dt-ffi/ptr->struct (:datatype-name @admesh.stl/stl-file*)
                      stl-pointer)
  ;;
  )
