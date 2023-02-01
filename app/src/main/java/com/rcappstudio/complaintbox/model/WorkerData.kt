package com.rcappstudio.complaintbox.model

data class WorkerData(
    val name: String ?= null,
    val token: String ?= null,
    val assignments: HashMap<String, ComplaintId> ?= null
)