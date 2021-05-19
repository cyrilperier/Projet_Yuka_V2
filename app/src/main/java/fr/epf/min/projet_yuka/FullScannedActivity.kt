package fr.epf.min.projet_yuka

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Bundle

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.room.Room
import com.google.zxing.Result
import fr.epf.min.projet_yuka.data.AccesDataProduct
import fr.epf.min.projet_yuka.data.FavoriDao
import fr.epf.min.projet_yuka.data.FavoriDataBase
import fr.epf.min.projet_yuka.data.GetProductsResult
import fr.epf.min.projet_yuka.model.Favori
import kotlinx.coroutines.runBlocking
import me.dm7.barcodescanner.zxing.ZXingScannerView
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*


class FullScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    lateinit var mScannerView: ZXingScannerView
    var mFlash = false
    var mAutoFocus = false
    var mSelectedIndices: ArrayList<Int>? = null
    var mClss: Class<*>? = null
    lateinit var favoris: MutableList<Favori>
    lateinit var database: FavoriDataBase
    lateinit var favoriDao: FavoriDao
    lateinit var Product: Favori
    lateinit var result: GetProductsResult
    var already_favori =true


    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        
        Dao()
        launchActivity()
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false)
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true)
        } else {
            mFlash = false
            mAutoFocus = true
            mSelectedIndices = null
        }
        setContentView(R.layout.activity_full_scanner)

        val contentFrame = findViewById<ViewGroup>(R.id.content_frame)
        mScannerView = ZXingScannerView(this)
        contentFrame.addView(mScannerView)
    }

    override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera(-1)
        mScannerView!!.flash = mFlash
        mScannerView!!.setAutoFocus(mAutoFocus)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(FLASH_STATE, mFlash)
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        var menuItem: MenuItem?
        if (mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on)
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off)
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)
        if (mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on)
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off)

        }

        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)
        menuItem = menu.add(Menu.NONE, R.id.favori_action, 0, "Favori")
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)
        menuItem = menu.add(Menu.NONE, R.id.APropo_action, 0, "A propo")
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.menu_flash -> {
                mFlash = !mFlash
                if (mFlash) {
                    item.setTitle(R.string.flash_on)
                } else {
                    item.setTitle(R.string.flash_off)
                }
                mScannerView!!.flash = mFlash
                true
            }
            R.id.menu_auto_focus -> {
                mAutoFocus = !mAutoFocus
                if (mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on)
                } else {
                    item.setTitle(R.string.auto_focus_off)
                }
                mScannerView!!.setAutoFocus(mAutoFocus)
                true
            }
            R.id.favori_action -> {
                val intent = Intent(this, ListFavoriActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.APropo_action->{
                AlertDialog.Builder(this)
                        .setTitle("A propo de l'application")
                        .setMessage("Nom : Projet Yuka \nVersion : 1 \nDéveloppeur : Cyril PERIER / Henri FORJOT")
                        .setPositiveButton("Ok"){
                            _,_ ->
                        }.show()

                true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun handleResult(rawResult: Result) {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
        }
try{
        var code = rawResult.text.toLong()
        Recuperation_info_product(code)
        runBlocking {

            try {
                 Product =favoriDao.findByCode(result.product.code)
                already_favori = true
            } catch (e:IllegalArgumentException){
                already_favori = false
            }
        }


        val scan=true
        val intent = Intent(this, DetailFavoriActivity::class.java)
        intent.putExtra("code", code);
    Log.d("epf", code.toString())
        intent.putExtra("already_favori", already_favori)
        intent.putExtra("scan", scan)
        startActivity(intent)}catch(e:Exception){

            Toast.makeText(this,"Le produit n'est pas référencé, scanner un autre produit s'il vous plait", Toast.LENGTH_LONG).show()
        }

        mScannerView!!.resumeCameraPreview(this)

    }

    override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()
    }

    private fun launchActivity() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                !== PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), ZXING_CAMERA_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ZXING_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mClss != null) {
                        val intent = Intent(this, mClss)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun Dao() {
        //accés a la base
        database = Room.databaseBuilder(
                this, FavoriDataBase::class.java, "favoris-db"

        ).build()
        favoriDao = database.getFavoriDao()
    }

    companion object {
        private const val FLASH_STATE = "FLASH_STATE"
        private const val AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE"
        private const val ZXING_CAMERA_PERMISSION = 1
    }

    fun Recuperation_info_product(code: Long) {

        runBlocking {
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://world.openfoodfacts.org/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
            val service = retrofit.create(AccesDataProduct::class.java)
            result = service.getProducts(code)
            Log.d("EPF", "$result")
        }
    }
}