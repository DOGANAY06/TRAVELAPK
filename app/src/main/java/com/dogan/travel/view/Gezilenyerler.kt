package com.dogan.travel.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogan.travel.FotografPaylasma
import com.dogan.travel.R
import com.dogan.travel.adapter.GezilenyerReclerAdapter
import com.dogan.travel.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_gezilenyerler.*
import androidx.recyclerview.widget.RecyclerView.LayoutManager as LayoutManager

class Gezilenyerler : AppCompatActivity() {
    private lateinit var  auth: FirebaseAuth
    private lateinit var database :FirebaseFirestore
    private lateinit var recyclerViewAdapter: GezilenyerReclerAdapter //recyclerview adapter tanımladık
    var postListesi  = ArrayList<Post>() //post sınıfının elemanlarini tutucaz
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gezilenyerler)
        auth=FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        verileriAl()
        var layoutManager =LinearLayoutManager(this)
        recyclerview.layoutManager = layoutManager
        recyclerViewAdapter = GezilenyerReclerAdapter(postListesi)
        recyclerview.adapter =recyclerViewAdapter //recyclerviewde kullanılacağını söyledik
    }
    fun verileriAl(){
        database.collection("Post").orderBy("tarih",com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception -> //tarihe göre dizdiricez en son tarih en başta olucak
            if (exception != null){
                Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show() //hata varsa hatayı gösterir
            }
            else{
                if (snapshot != null){
                    if (snapshot.isEmpty ==false) { //boş değilse snapshot
                        //kesin veri vardır snaphshot da
                        val documents = snapshot.documents
                        postListesi.clear() //her seferinde liste temizlenir
                        for (document in documents){ //bütün dokumanları dokuman değişkenine at
                            val kullaniciemail =document.get("kullaniciemail") as String
                            val kullaniciyorum =document.get("kullaniciyorum") as String
                            var gorselUrl =document.get("gorselurl") as String

                            val indirilenPost = Post(
                                kullaniciemail,
                                kullaniciyorum,
                                gorselUrl
                            )
                            postListesi.add(indirilenPost) //kendi sınıfımıza kaydediyoruz
                        }
                        recyclerViewAdapter.notifyDataSetChanged() //yeni veri geldi kendini yenile her recyclerviewden sonra
                    }
                }
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //menu çağırma işlevi yaptım
        val menuInflater =getMenuInflater()
        menuInflater.inflate(R.menu.secenekler_menusu,menu) //kullanılacak menuyu belirledik directory içinde
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.fotograf_paylas)
        {
            //fotograf paylaşılacak activity gidilecek
            val intent =Intent(this, FotografPaylasma::class.java)
            startActivity(intent)

        }
        else if (item.itemId == R.id.cikis_yap){
            auth.signOut() //veritabanınıda kapat anlamında
            val intent =Intent(this, KullaniciActivity::class.java)
            startActivity(intent)
            //cikis yapma secilirse kullaniciactivity yani ilk activity gider
            finish()

        }
        return super.onOptionsItemSelected(item)
    }
}