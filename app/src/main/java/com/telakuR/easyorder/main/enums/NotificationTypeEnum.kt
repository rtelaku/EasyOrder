package com.telakuR.easyorder.main.enums

import com.telakuR.easyorder.R
import com.telakuR.easyorder.main.models.NotificationModel
import com.telakuR.easyorder.utils.EasyOrder

enum class NotificationTypeEnum(val id: Double) {
    NEW_ORDER(1.0),
    NEW_MENU_ITEM(1.1),
    ACCEPTED_TO_COMPANY(2.0),
    REJECTED_FROM_COMPANY(2.1),
    NEW_EMPLOYEE_REQUEST(3.0);

    companion object {
        fun getMessageBasedOnNotification(notification: NotificationModel): String {
            val context = EasyOrder.getInstance().applicationContext

            return when(notification.id) {
                NEW_ORDER.id -> String.format(context.getString(R.string.ordering_notification), notification.ownerName, notification.fastFood)
                NEW_MENU_ITEM.id -> String.format(context.getString(R.string.employee_added_menu_item), notification.ownerName)
                ACCEPTED_TO_COMPANY.id -> String.format(context.getString(R.string.you_have_been_accepted_to_company), notification.ownerName)
                REJECTED_FROM_COMPANY.id -> String.format(context.getString(R.string.you_have_been_declined_from_company), notification.ownerName)
                NEW_EMPLOYEE_REQUEST.id -> String.format(context.getString(R.string.employee_requesting_to_join_company), notification.ownerName)
                else -> { "" }
            }
        }
    }
}
