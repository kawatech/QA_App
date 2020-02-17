package jp.techacademy.naoki.kawamata.qa_app

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.widget.ListView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_favorities.*

class FavoritiesActivity : AppCompatActivity() {

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mQuestion: Question
    private lateinit var mFavoriteUidRef: DatabaseReference
    private lateinit var mCheckFavQidRef: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mQuestionArrayList: ArrayList<Question>
    private lateinit var mAdapter: QuestionsListAdapter

    private val mFavoriteUidListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            // ここでのkeyはfavoriteのユーザーIDの下の質問IDになる
            // この質問IDに紐づくgenreを取得する
            val favoriteQid = dataSnapshot.key ?: ""
            val map = dataSnapshot.value as Map<String, String>
            val fgenre = map["genre"]

            // Preferenceからログイン中のユーザーIDを取得する
            val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val curruid = sp.getString(LoginID, "")
            val currQuestionUid = mQuestion.questionUid
            // ログインユーザーIDと同じものがあれば、ボタン表示を削除にする
            // ユーザーIDが一致してかつQuestionUidで一致するものがあれば、削除ボタンにする


            val dataBaseReference = FirebaseDatabase.getInstance().reference
            mCheckFavQidRef = dataBaseReference.child(ContentsPATH).child(fgenre.toString()).child(favoriteQid)

            mCheckFavQidRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val map = dataSnapshot.value as Map<String, String>
                    val title = map["title"] ?: ""
                    val body = map["body"] ?: ""
                    val name = map["name"] ?: ""
                    val uid = map["uid"] ?: ""
                    val imageString = map["image"] ?: ""
                    val bytes =
                        if (imageString.isNotEmpty()) {
                            Base64.decode(imageString, Base64.DEFAULT)
                        } else {
                            byteArrayOf()
                        }

                    val answerArrayList = ArrayList<Answer>()

                    val answerMap = map["answers"] as Map<String, String>?
                    if (answerMap != null) {
                        for (key in answerMap.keys) {
                            val temp = answerMap[key] as Map<String, String>
                            val answerBody = temp["body"] ?: ""
                            val answerName = temp["name"] ?: ""
                            val answerUid = temp["uid"] ?: ""
                            val answer = Answer(answerBody, answerName, answerUid, key)
                            answerArrayList.add(answer)
                        }
                    }



                    val question = Question(title, body, name, uid, dataSnapshot.key ?: "",
                        1, bytes, answerArrayList)

             //      mListView = findViewById(R.id.listView)       // onCreate()に移動した
             //       mAdapter = QuestionsListAdapter(applicationContext)       // onCreate()に移動した
             //       mQuestionArrayList = ArrayList<Question>()       // onCreate()に移動した

                    mQuestionArrayList.add(question)

                    mAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(firebaseError: DatabaseError) {}       // これがないと、object is not abstruct....が出る
            })

        }
        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorities)

        mListView = findViewById(R.id.listView)
        mAdapter = QuestionsListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()
        mAdapter.notifyDataSetChanged()


        // 渡ってきたQuestionのオブジェクトを保持する、これがないと、上のmQuestionの参照で停止する
        val extras = intent.extras
        mQuestion = extras.get("question") as Question


        // Preferenceからログイン中のユーザーIDを取得する
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val curruid = sp.getString(LoginID, "")

        val mDatabaseReference = FirebaseDatabase.getInstance().reference
        mAdapter.setQuestionArrayList(mQuestionArrayList)
        mListView.adapter = mAdapter
        mFavoriteUidRef = mDatabaseReference.child(FavoritePATH).child(curruid)
        mFavoriteUidRef.addChildEventListener(mFavoriteUidListener)




        setSupportActionBar(toolbar)
        title = "お気に入り一覧"
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    // --- 課題---
    override fun onResume() {
        super.onResume()
/*
    // Preferenceからログイン中のユーザーIDを取得する
    // ここにあると、画面を再表示するたびに表示が繰り返されて長くなってしまう

        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val curruid = sp.getString(LoginID, "")

        val mDatabaseReference = FirebaseDatabase.getInstance().reference
        mAdapter.setQuestionArrayList(mQuestionArrayList)
        mListView.adapter = mAdapter
        mFavoriteUidRef = mDatabaseReference.child(FavoritePATH).child(curruid)
        mFavoriteUidRef.addChildEventListener(mFavoriteUidListener)
 */


    //    mAdapter.setQuestionArrayList(mQuestionArrayList)
    //    mListView.adapter = mAdapter
    //    mAdapter.notifyDataSetChanged()

    }

}
