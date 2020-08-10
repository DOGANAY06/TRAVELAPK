package com.dogan.travel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dogan.travel.R
import com.dogan.travel.model.Post
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_row.view.*

class GezilenyerReclerAdapter (val postList :ArrayList<Post>) :RecyclerView.Adapter<GezilenyerReclerAdapter.PostHolder>(){
    class PostHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        //recycler_row xml bağlama işlemi yapılır
        val inflater =LayoutInflater.from(parent.context)
        val view =inflater.inflate(R.layout.recycler_row,parent,false)
        return PostHolder(view)
    }

    override fun getItemCount(): Int { //kaç tane eleman olacağını söyler
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        //recyclerview deki şeyleri alıyoruz
        holder.itemView.recycler_row_kullanici_emaili.text =postList[position].kullaniciemail
        holder.itemView.recycler_row_yorum.text =postList[position].kullaniciyorum
        Picasso.get().load(postList[position].gorselUrl).into(holder.itemView.recycler_row_resim) //recycler row de bulunan resim
        //bolumune picasso kütüphanesi ile resmi indirtip ekledik resmin pozisyonuyla beraber

    }
}
