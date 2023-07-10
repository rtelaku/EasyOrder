package com.telakuR.easyorder.main.enums

import com.telakuR.easyorder.R
import com.telakuR.easyorder.room_db.enitites.NotificationModel

enum class NotificationTypeEnum(val id: Double) {
    NEW_ORDER(1.0),
    NEW_MENU_ITEM(1.1),
    ACCEPTED_TO_COMPANY(2.0),
    REJECTED_FROM_COMPANY(2.1),
    NEW_EMPLOYEE_REQUEST(3.0);

    companion object {
        fun getMessageBasedOnNotification(notification: NotificationModel): Pair<Int?, Array<String?>> {
            return when(notification.id) {
                NEW_ORDER.id -> Pair(R.string.ordering_notification, arrayOf(notification.ownerName, notification.fastFood))
                NEW_MENU_ITEM.id ->  Pair(R.string.employee_added_menu_item, arrayOf(notification.ownerName))
                ACCEPTED_TO_COMPANY.id ->  Pair(R.string.you_have_been_accepted_to_company, arrayOf(notification.ownerName))
                REJECTED_FROM_COMPANY.id ->  Pair(R.string.you_have_been_declined_from_company, arrayOf(notification.ownerName))
                NEW_EMPLOYEE_REQUEST.id ->  Pair(R.string.employee_requesting_to_join_company, arrayOf(notification.ownerName))
                else -> {  Pair(null, emptyArray<String?>()) }
            }
        }
    }
}
