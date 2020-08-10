package com.dogan.travel.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.dogan.travel.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class KullaniciActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this) {} //REKLAMLAR İÇİN OLAN kısım
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        auth = FirebaseAuth.getInstance() //firabase tanımlama

        val guncelKullanici =auth.currentUser
        if (guncelKullanici != null){
            val intent = Intent(this, Gezilenyerler::class.java)
            startActivity(intent)
            finish()

        }
    }
    fun girisyap(view: View){
        auth.signInWithEmailAndPassword(emailtext.text.toString(),passwordtext.text.toString()).addOnCompleteListener { task ->
            //e mail ve password aliyoruz edittexten sign in için
            if(task.isSuccessful){ //giris yapma işlevi doğru mu
                val guncelKullanici = auth.currentUser?.email.toString()
                Toast.makeText(this,"Hoşgeldin = ${guncelKullanici}", Toast.LENGTH_LONG).show()

                val intent = Intent(this, Gezilenyerler::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_LONG).show()

        }


    }
    fun kayit(view: View){
        val email =emailtext.text.toString()
        val sifre =passwordtext.text.toString()
        //email sifre gecerliyse
        auth.createUserWithEmailAndPassword(email,sifre).addOnCompleteListener { task ->
            if (task.isSuccessful){ //kayit olma işlemi başarılıysa
                //diger aktiviteye gideriz
                val intent = Intent(this, Gezilenyerler::class.java)
                startActivity(intent)
                finish() //activitye git ve bitir geri gelme olayını
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()

        }
    }
}