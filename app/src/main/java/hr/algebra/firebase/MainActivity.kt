package hr.algebra.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.firebase.databinding.ActivityMainBinding
import hr.algebra.firebase.model.Todo
import hr.algebra.firebase.ui.OnItemClickListener
import hr.algebra.firebase.ui.TodosAdapter

class MainActivity : AppCompatActivity( ) {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        binding = ActivityMainBinding.inflate( layoutInflater )
        setContentView( binding.root )

        binding.bButton.setOnClickListener {
            if( validate( ) ) {
                Toast.makeText(this, "Dodajem zadatak", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText( this, "Please provide needed data.", Toast.LENGTH_SHORT ).show( )
        }

        setupList( )
    }

    private fun setupList( ) {
        val x = listOf( Todo( "Ujutro", "Dorucak" ), Todo( "Uvecer", "Vecera" ) )
        binding.rvRecycler.layoutManager = LinearLayoutManager( this )
        binding.rvRecycler.adapter = TodosAdapter( x, object : OnItemClickListener {
            override fun onClick( todo : Todo ) {
                Toast.makeText( this@MainActivity, todo.toString( ), Toast.LENGTH_SHORT ).show( )
            }
        } )
    }

    private fun validate( ) : Boolean {
        val title       = binding.etPrvi.text.toString( ).trim( )
        val description = binding.etDrugi.text.toString( ).trim( )

        if( title=="" )       binding.etPrvi.error  = "Please insert Title"
        if( description=="" ) binding.etDrugi.error = "Please insert Description"

        return title!="" && description!=""
    }
}