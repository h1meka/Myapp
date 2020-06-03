package app.watanabe.watahime.myapp

import android.content.IntentFilter.create
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import java.net.URI.create
import java.util.*

data class Task(val id: String, val time: Long, val name: String) {
    companion object {
        fun create(name: String): Task = Task(UUID.randomUUID().toString(), Date().time, name)
    }

class TaskRepository {
    private val database : FirebaseFirestore get() = FirebaseFirestore.getInstance()

    suspend fun add(task: Task): Boolean {
        try {
            val collection = database.collection(COLLECTION_PATH)
            val document = collection.document(task.id)
            val data = task.toHashMap()
            document.set(data).await()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun Task.toMap(): Map<String, *> {
        return hashMapOf(
            "id" to this.id,
            "time" to this.time,
            "name" to this.name
        )
    }

    suspend fun delete(task: Task): Boolean {
        try {
            val collection = database.collection(COLLECTION_PATH)
            val document = collection.document(task.id)
            document.delete().await()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun fetchTask(limit: Long): List<Task> {
        try {
            val collection = database.collection("CollectionName")
            val documents = collection.limit(limit).get().documents
            return documents.map { it.data }.mapNotNull { it.toTask() }
        } catch (e: Exception) {
            return listOf()
        }
    }

    fun Map<String, Any>.toTask(): Task {

        val id = this["id"] as String
        val time = this["time"] as Long
        val name = this["name"] as String
        return Task(id, time, name)
    }

    companion object {
        private const val COLLECTION_PATH = "tasks"
    }
}

class MainActivity : AppCompatActivity() {

    private val repository : TaskRepository = TaskRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }
}
}