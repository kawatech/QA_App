package jp.techacademy.naoki.kawamata.qa_app

import java.io.Serializable
import java.util.*

class Question(val title: String, val body: String, val name: String, val uid: String, val questionUid: String, val genre: Int, bytes: ByteArray, val answers: ArrayList<Answer>) : Serializable {
//class Question(val title: String, val body: String, val name: String, val uid: String, val questionUid: String, val genre: Int,
  //             bytes: ByteArray, val answers: ArrayList<Answer>, val favorite: ArrayList<String>) : Serializable {

    val imageBytes: ByteArray

    init {
        imageBytes = bytes.clone()
    }
}