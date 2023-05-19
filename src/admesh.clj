(ns admesh
  (:require
   [admesh.stl]
   [tech.v3.datatype.ffi :as dt-ffi]
   [tech.v3.datatype.native-buffer :as native-buffer]))

(defonce lib (dt-ffi/library-singleton #'admesh.stl/fns))
(dt-ffi/library-singleton-reset! lib)

(dt-ffi/define-library-functions
  admesh.stl/fns
  (fn [fn-name] (dt-ffi/library-singleton-find-fn lib fn-name))
  nil)

(comment
  (->> "/tmp/demo/libadmesh.dylib" (dt-ffi/library-singleton-set! lib))

  (def p (native-buffer/malloc
          (:datatype-size @admesh.stl/stl-file*)
          {:resource-type nil}))

  ;; Free the buffer when done playing.
  (native-buffer/free p)

  (stl_open p "/tmp/demo/demo.stl")
  (stl_calculate_volume p)
  (stl_calculate_surface_area p)
  (stl_close p)
  (stl_get_error p)

  ;; direct access via address offset
  (.getFloat (native-buffer/unsafe) (+ (.address p) 208))

  ;; access via struct
  (def s (dt-ffi/ptr->struct (:datatype-name @admesh.stl/stl-file*) p))

  (dt-ffi/ptr->struct
   (:datatype-name @admesh.stl/stl-facet*)
   (tech.v3.datatype.ffi.Pointer. (:facet-start s)))

  (dt-ffi/ptr->struct
   (:datatype-name @admesh.stl/stl-neighbors*)
   (tech.v3.datatype.ffi.Pointer. (:neighbors-start s)))

  ;;
  )
