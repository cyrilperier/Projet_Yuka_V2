package fr.epf.min.projet_yuka


import android.os.Bundle

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room

import fr.epf.min.projet_yuka.data.FavoriDao
import fr.epf.min.projet_yuka.data.FavoriDataBase
import fr.epf.min.projet_yuka.model.Favori

import kotlinx.android.synthetic.main.activity_list_favori.*
import kotlinx.coroutines.runBlocking


class ListFavoriActivity : AppCompatActivity() {

    lateinit var favoris: MutableList<Favori>
    lateinit var database: FavoriDataBase
    lateinit var favoriDao: FavoriDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_favori)

        list_favoris_recyclerview.layoutManager =
                LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                )
    }
    override fun onStart() {


        super.onStart()
        Dao()


        runBlocking {  favoris = favoriDao.getAllFavoris().toMutableList()
            list_favoris_recyclerview.adapter = ListFavoriAdapter(favoris,this@ListFavoriActivity)
        }

    }

    private fun Dao(){
        //accés a la base
        database = Room.databaseBuilder(
                this, FavoriDataBase::class.java, "favoris-db"

        ).build()
        favoriDao = database.getFavoriDao()
    }
}