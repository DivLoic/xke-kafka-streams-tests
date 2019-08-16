package fr.xebia.ldi.crocodile.stream

import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.{KeyValueMapper, Predicate, ValueJoiner, ValueMapper}

/**
  * Created by loicmdivad.
  */
trait CrocoConversion {

  // ONLY FOR PRESENTATION PURPOSE
  // THIS WILL HELP JAVA AND KOTLIN DEVS TO IGNORE THE FANCY SCALA DSL
  // AND FOCUS ON WHATS MATTER... THE TESTS!

  implicit def predicate4s[K, V](p: Predicate[K, V]): (K, V) => Boolean = p.test

  implicit def joiner4s[V, VT, VR](vj: ValueJoiner[V, VT, VR]): (V, VT) => VR = vj.apply

  implicit def vMapper4s[V, VR](vm: ValueMapper[V, VR]): V => VR = vm.apply

  implicit def kvMapper4s[K, V, KR >: Null, VR >: Null]
  (kvm: KeyValueMapper[K, V, KeyValue[KR, VR]]): (K, V) => (KR, VR) = {
    case (key, value) => kvm.apply(key, value) match {
      case null => (null, null)
      case record => (record.key, record.value)
    }
  }
}
