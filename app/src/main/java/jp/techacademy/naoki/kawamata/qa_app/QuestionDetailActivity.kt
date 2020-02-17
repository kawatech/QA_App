package jp.techacademy.naoki.kawamata.qa_app

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_question_detail.*

class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    private lateinit var mFavoriteRef: DatabaseReference
    private lateinit var mCheckFavRef: DatabaseReference

    // favoriteの確認用フラグ、一致するものがあったらtureにする
    private var mIsCheckFav = false

    // onChild*() の関数はやることが無くても全部入れておく。
    private val mFavoriteListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val favoriteUid = dataSnapshot.key ?: ""

            // Preferenceからログイン中のユーザーIDを取得する
            val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val curruid = sp.getString(LoginID, "")
            val currQuestionUid = mQuestion.questionUid
            // ログインユーザーIDと同じものがあれば、ボタン表示を削除にする
            // ユーザーIDが一致してかつQuestionUidで一致するものがあれば、削除ボタンにする

       //     if(mIsCheckFav != true) {
                val dataBaseReference = FirebaseDatabase.getInstance().reference
                mCheckFavRef =
                    dataBaseReference.child(FavoritePATH).child(favoriteUid).child(currQuestionUid)
                mCheckFavRef.addChildEventListener(mCheckFavListener)
        //    }
            // mIsCheckFavがtrueなら一致するものがあった
        //    if (mIsCheckFav) {
              //  favoriteBtnAdd.setVisibility(View.GONE)
              //  favoriteBtnDel.setVisibility(View.VISIBLE)
        //    }


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

    private val mCheckFavListener = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            mIsCheckFav = true
            favoriteBtnAdd.setVisibility(View.GONE)
            favoriteBtnDel.setVisibility(View.VISIBLE)
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






    private val mEventListener = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid == answer.answerUid) {
                    return
                }
            }

            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""

            val answer = Answer(body, name, uid, answerUid)
            mQuestion.answers.add(answer)
            mAdapter.notifyDataSetChanged()
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
        setContentView(R.layout.activity_question_detail)

        // 渡ってきたQuestionのオブジェクトを保持する
        val extras = intent.extras
        mQuestion = extras.get("question") as Question

        title = mQuestion.title


                // ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()



        val dataBaseReference = FirebaseDatabase.getInstance().reference

        // Preferenceからログイン中のユーザーIDを取得する
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val curruid = sp.getString(LoginID, "")

        // お気に入りボタンをタップしたとき、ログインしているユーザーの「お気に入り」に登録
        favoriteBtnAdd.setOnClickListener { v ->
     //       val uid = mQuestion.uid
            val quid = mQuestion.questionUid
            val genre = mQuestion.genre

            val dataBaseReference = FirebaseDatabase.getInstance().reference
        //    val favoritRef = dataBaseReference.child(FavoritePATH).child(uid).child(quid)
            val favoritRef = dataBaseReference.child(FavoritePATH).child(curruid).child(quid)
            val data = HashMap<String, String>()
            data["genre"] = genre.toString()
            favoritRef.setValue(data)             // 登録するとき
        }

        // 「お気に入り（削除）」ボタンをタッチしたとき
        favoriteBtnDel.setOnClickListener { v ->
     //       val uid = mQuestion.uid
            val quid = mQuestion.questionUid

            val dataBaseReference = FirebaseDatabase.getInstance().reference
         //   val favoritRef = dataBaseReference.child(FavoritePATH).child(uid).child(quid)
            val favoritRef = dataBaseReference.child(FavoritePATH).child(curruid).child(quid)

            favoritRef.removeValue()                 // 削除するとき

            // ボタンを登録にする
            favoriteBtnDel.setVisibility(View.GONE)
            favoriteBtnAdd.setVisibility(View.VISIBLE)

        }


        fab.setOnClickListener {
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Questionを渡して回答作成画面を起動する
                // --- ここから ---
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                startActivity(intent)
                // --- ここまで ---
            }
        }

   //     val dataBaseReference = FirebaseDatabase.getInstance().reference
        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString()).child(mQuestion.questionUid).child(AnswersPATH)
        mAnswerRef.addChildEventListener(mEventListener)
    }


    // --- 課題---
    override fun onResume() {
        super.onResume()
        // ログインなら「お気に入り」ボタン表示する。そうでなければ非表示
        // ボタンの表示、非表示は判定の場所で行う

        // favoriteの情報を取る、ログインしていたら
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val dataBaseReference = FirebaseDatabase.getInstance().reference
            mFavoriteRef = dataBaseReference.child(FavoritePATH)
            mFavoriteRef.addChildEventListener(mFavoriteListener)
            // ここでは登録ボタンとしておく
            favoriteBtnAdd.setVisibility(View.VISIBLE)
        }

    }

    // --- ここまで---
}