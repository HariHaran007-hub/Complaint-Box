package com.rcappstudio.complaintbox.model


data class Complaint
    (val title: String? = null,
     val location: String? = null,
     val imageUrl: String? = null,
     val description: String?= null,
     val videoUrl: String ?= null,
     val department : String ?= null,
     val timeStamp : Long ?= null,
     val userId: String ?= null,
     val complaintId: String ?= null,
     val note: String ?= "Problem will be sort out soon",
     val solved: Int ?= 0,
     val imageUrlList: List<String> ?= null
     )
