
package com.example.api
import androidx.appcompat.widget.SearchView
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu

import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Collections

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var myAdapter: MyAdapter
    lateinit var productList: ArrayList<Product>
    lateinit var tempArraylist: ArrayList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        productList = ArrayList()
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getProductData()

        retrofitData.enqueue(object : Callback<MyData?> {

            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                // if api call is a success, then use the data of API and show in your app
                var responseBody = response.body()
                val productList = responseBody?.products!!
                tempArraylist= responseBody.products

                myAdapter = MyAdapter(this@MainActivity, tempArraylist)
                val swipeGesture = object: SwipeGesture(this@MainActivity){
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ) :Boolean{
                        val fromPos = viewHolder.adapterPosition
                        val toPos = target.adapterPosition
                        Collections.swap(productList,fromPos,toPos)
                        myAdapter.notifyItemMoved(fromPos,toPos)
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {



                        when(direction){
                            ItemTouchHelper.LEFT -> {
                                myAdapter.deleteitem(viewHolder.adapterPosition)
                            }
                            ItemTouchHelper.RIGHT -> {
                                val x= productList[viewHolder.adapterPosition]
                                myAdapter.deleteitem(viewHolder.adapterPosition)
                                myAdapter.additem(productList.size,x)

                            }

                        }

                    }

                }
                val touchHelper = ItemTouchHelper(swipeGesture)
                touchHelper.attachToRecyclerView(recyclerView)
                recyclerView.adapter = myAdapter
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)


            }

            override fun onFailure(call: Call<MyData?>, t: Throwable) {
                // if api call fails
                Log.d("Main Activity ", "onFailure: " + t.message)
            }
        })



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item,menu)
        val item=menu?.findItem(R.id.search_action)
        val searchView=item?.actionView as SearchView
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempArraylist.clear()
                val searchText = newText!!.toLowerCase()
                if (searchText.isNotEmpty()) {
                    productList.forEach {
                        if (it.title.toLowerCase().contains(searchText)) {
                            tempArraylist.add(it)
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    //tempArraylist.clear()
                    tempArraylist.addAll(productList)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }



                return false
            }

        })





        return true

    }
}
