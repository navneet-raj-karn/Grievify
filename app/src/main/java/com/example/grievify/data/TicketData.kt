package com.example.grievify.data


data class TicketData(val category: String? = null,
                      val description: String? = null,
                      val docsList: ArrayList<String>?=null,
                      val feedback: String? = null,
                      val priority: String? = null,
                      val resolvedMsg: String? = null,
                      val status: String? = null,
                      val ticketID: String? = null,
                      val userID: String? = null,
                      val userName: String? = null)
{

}

//val imageList:ArrayList<String>?=null,