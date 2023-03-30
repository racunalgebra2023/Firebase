package hr.algebra.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.algebra.firebase.databinding.ActivityMainBinding
import hr.algebra.firebase.model.Todo
import hr.algebra.firebase.ui.OnItemClickListener
import hr.algebra.firebase.ui.TodosAdapter

const val TODOS_COLLECTION = "NewTodos"
const val TODOS_NAME = "name"
const val TODOS_DESCRIPTION = "desc"
const val COMPONENT_NAME = "MainActivity"

class MainActivity : AppCompatActivity( ) {

    private val TAG ="MainActivity"

    private lateinit var binding : ActivityMainBinding
    private val todos : MutableList< Todo > = mutableListOf( )

    private lateinit var db        : FirebaseFirestore
    private lateinit var analytics : FirebaseAnalytics

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        binding = ActivityMainBinding.inflate( layoutInflater )
        setContentView( binding.root )

        db        = Firebase.firestore
        analytics = Firebase.analytics

        getFreshList( )
        binding.bButton.setOnClickListener {
            if( validate( ) ) {
                val todo = hashMapOf(
                    TODOS_NAME        to binding.etPrvi.text.toString( ).trim( ),
                    TODOS_DESCRIPTION to binding.etDrugi.text.toString( ).trim( )
                )
                db.collection( TODOS_COLLECTION )
                    .add( todo )
                    .addOnSuccessListener { documentReference ->
                        Log.e( TAG, "New Todo $todo added with ID: ${ documentReference.id }" )
                        Toast.makeText( this, "Zadatak dodan...", Toast.LENGTH_SHORT ).show( )
                        binding.etPrvi.setText( "" )
                        binding.etDrugi.setText( "" )
                        // getFreshList( )
                    }
                    .addOnFailureListener { e ->
                        Log.e( TAG, "Error adding document", e )
                        Toast.makeText( this, "Nisam uspio dodati zadatak...", Toast.LENGTH_SHORT ).show( )
                    }
            } else
                Toast.makeText( this, "Please provide needed data.", Toast.LENGTH_SHORT ).show( )
        }

        setupList( )
        logEvent( FirebaseAnalytics.Event.APP_OPEN )
    }

    private fun setupList( ) {
        binding.rvRecycler.layoutManager = LinearLayoutManager( this )
        binding.rvRecycler.adapter = TodosAdapter( todos, object : OnItemClickListener {
            override fun onClick( todo : Todo ) {
                db.collection( TODOS_COLLECTION ).document( todo.id )
                    .delete( )
                    .addOnSuccessListener {
                        Log.e(TAG, "DocumentSnapshot successfully deleted!")
                        // getFreshList( )
                    }
                    .addOnFailureListener { e -> Log.e(TAG, "Error deleting document", e) }
            }
        } )
    }

    private fun getFreshList( ) {
        Log.e( TAG, "Idem po zadatke..." )
/*
        db.collection( TODOS_COLLECTION )
            .get( )
            .addOnSuccessListener { result ->
                todos.clear( )
                for ( document in result ) {
                    Log.e(TAG, "${ document.id } => ${ document.data }")
                    val id   = document.id
                    val name = document.data[TODOS_NAME] as String?
                    val desc = document.data[TODOS_DESCRIPTION] as String?
                    val todo = Todo( id, name ?: "", desc ?: "" )
                    todos.add( todo )
                }
                binding.rvRecycler.adapter?.notifyDataSetChanged( )
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents.", exception)
            }
*/
        db.collection( TODOS_COLLECTION ) .addSnapshotListener  { result, e ->
                todos.clear( )
                for ( document in result!! ) {
                    Log.e(TAG, "${ document.id } => ${ document.data }")
                    val id   = document.id
                    val name = document.data[TODOS_NAME] as String?
                    val desc = document.data[TODOS_DESCRIPTION] as String?
                    val todo = Todo( id, name ?: "", desc ?: "" )
                    todos.add( todo )
                }
                binding.rvRecycler.adapter?.notifyDataSetChanged( )
            }
    }

    private fun validate( ) : Boolean {
        val title       = binding.etPrvi.text.toString( ).trim( )
        val description = binding.etDrugi.text.toString( ).trim( )

        if( title=="" )       binding.etPrvi.error  = "Please insert Title"
        if( description=="" ) binding.etDrugi.error = "Please insert Description"

        return title!="" && description!=""
    }

    private fun logEvent( eventName:String ) {
        val bundle = Bundle( )
        bundle.putString( FirebaseAnalytics.Param.ITEM_ID, COMPONENT_NAME )
        analytics.logEvent( eventName, bundle )
    }
}