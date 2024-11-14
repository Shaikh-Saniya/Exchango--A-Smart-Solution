package com.example.exchango.activity.invite.adapter

import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.example.exchango.activity.invite.adapter.Invite

class ContactsPagingSource(private val contentResolver: ContentResolver) : PagingSource<Int, Invite>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Invite> {
        val page = params.key ?: 0
        val offset = page * params.loadSize
        val contacts = mutableListOf<Invite>()

        try {
            val nameCursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID),
                null,
                null,
                "${ContactsContract.Contacts.DISPLAY_NAME} ASC LIMIT ${params.loadSize} OFFSET $offset"
            )

            nameCursor?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)

                while (cursor.moveToNext()) {
                    val name = cursor.getString(nameIndex)
                    val id = cursor.getString(idIndex)

                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(id),
                        null
                    )

                    phoneCursor?.use { phoneCursor ->
                        val numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        if (phoneCursor.moveToFirst()) {
                            val number = phoneCursor.getString(numberIndex)
                            contacts.add(Invite(name, number))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

        return LoadResult.Page(
            data = contacts,
            prevKey = if (page == 0) null else page - 1,
            nextKey = if (contacts.isEmpty()) null else page + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Invite>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val page = state.closestPageToPosition(anchorPosition)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }
}
