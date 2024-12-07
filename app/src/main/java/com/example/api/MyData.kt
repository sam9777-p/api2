package com.example.api

data class MyData(
    val limit: Int,
    val products: ArrayList<Product>,
    val skip: Int,
    val total: Int
)