package edu.android.thinkr.models

import android.os.Parcel
import android.os.Parcelable


/**
 * @author robin
 * Created on 10/17/20
 */
@Suppress("MemberVisibilityCanBePrivate")
class Subject(
      val chat_room_name : String,
      val image: Int,
      val cardBackgroundColor: Int,
      val chat_room_id : String
) :Parcelable {
     constructor(parcel: Parcel) : this(
          parcel.readString()!!,
          parcel.readInt(),
          parcel.readInt(),
          parcel.readString()!!
     ) {
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
          parcel.writeString(chat_room_name)
          parcel.writeInt(image)
          parcel.writeInt(cardBackgroundColor)
          parcel.writeString(chat_room_id)
     }

     override fun describeContents(): Int {
          return 0
     }

     companion object CREATOR : Parcelable.Creator<Subject> {
          override fun createFromParcel(parcel: Parcel): Subject {
               return Subject(parcel)
          }

          override fun newArray(size: Int): Array<Subject?> {
               return arrayOfNulls(size)
          }
     }
}
