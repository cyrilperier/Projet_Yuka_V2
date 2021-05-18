package fr.epf.min.projet_yuka.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName="favoris")
data class Favori(
        @PrimaryKey(autoGenerate = true) val id: Int?,
        val name:String,
        val nutri_score: String,
        val ingredients: String,
        val url_image_small: String,
        val url_image: String,
        val code:Long



){}