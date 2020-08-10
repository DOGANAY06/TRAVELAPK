package com.dogan.travel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_fotograf_paylasma.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.imageView
import java.util.*

class FotografPaylasma : AppCompatActivity() {

    var secilengorsel : Uri? =null
    var secilenBitmap : Bitmap? =null
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotograf_paylasma)
        storage = FirebaseStorage.getInstance()
        auth =FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }
    fun paylas(view: View){
        //depo işlemleri fotoğrafın paylasılması için
        //UUI -> universal unique id her resmin farklı bir id olması için
        val uuid =UUID.randomUUID()
        val gorselIsmi ="${uuid}.jpg" //gorsel isimleri için sıralı
        val reference = storage.reference //gorselin nereye kaydedileceğini söyledik
        val gorselReference =reference.child("images").child(gorselIsmi)
        if (secilengorsel != null){
            gorselReference.putFile(secilengorsel!!).addOnSuccessListener { taskSnapshot ->
                val yuklenenGorselReference = FirebaseStorage.getInstance().reference.child("images").child(gorselIsmi)
                yuklenenGorselReference.downloadUrl.addOnSuccessListener {uri ->
                    val downloadUrl =uri.toString()
                    val guncelKullaniciEmaili =auth.currentUser!!.email.toString() //kullanıcının emailini alıyoruz
                    val tarih =Timestamp.now() //zamanı alır kullanıcının yazdığı
                    val kullaniciyorumu =yorumtext.text.toString()
                    //veri tabanı işlemleri için hashmap oluşturdum
                    val postHashMap = hashMapOf<String,Any>()
                    postHashMap.put("gorselurl",downloadUrl)
                    postHashMap.put("kullaniciemail",guncelKullaniciEmaili)
                    postHashMap.put("tarih",tarih)
                    postHashMap.put("kullaniciyorum",kullaniciyorumu)
                    database.collection("Post").add(postHashMap).addOnCompleteListener {task ->
                        //database e posthashmapleri ekledik hepsini
                        if (task.isSuccessful){
                            finish() //başarıyla eklendi ve bitirdi
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show() //eklendikten sonra bekle
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show() //eklenmessede bir süre bekle ve hata mesajını ver
                }
            }
        }

    }
    fun gorselsec (view: View){ //imageview secicez
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
            //izin kontrolü yapıyoruz izin verilmediyse
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            //İZİN İSTEDİK SUANDA
        }else{ //izin verilmisse zaten yapılacaklar burada
            val galeriIntent =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //galerisine gidicez kişinin
            startActivityForResult(galeriIntent,2)

        }
    }

    override fun onRequestPermissionsResult( //izinler verildiyse sonuç ne olacak
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode ==1){
            if (grantResults.size>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                val galeriIntent =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //galerisine gidicez kişinin
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode ==2 && resultCode == Activity.RESULT_OK && data != null){ //galeriyi açtıktan sonra vazgecebilir kullanıcı  ve döndürmez ya da
            secilengorsel = data.data //bir adresi vardır datanın verinin
            if (secilengorsel!=null){
                if (Build.VERSION.SDK_INT >= 28){ //sdk 28 den büyükse resim almak için bu işlemler yapılır
                    val source =ImageDecoder.createSource(this.contentResolver,secilengorsel!!)
                    secilenBitmap =ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(secilenBitmap)
                }
                else{
                secilenBitmap =MediaStore.Images.Media.getBitmap(this.contentResolver,secilengorsel)
                imageView.setImageBitmap(secilenBitmap)
            }
            }
        }
        super.onActivityResult(requestCode, resultCode, data) //activity sonucunda ne olacak

    }
}