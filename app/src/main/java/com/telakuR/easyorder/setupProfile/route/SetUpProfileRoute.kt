package com.telakuR.easyorder.setupProfile.route

sealed class SetUpProfileRoute(val route: String) {
    object SelectPicture : SetUpProfileRoute(route = "selectPicture")
    object PicturePreview : SetUpProfileRoute(route = "picturePreview")
    object FindYourCompany : SetUpProfileRoute(route = "findYourCompany")
}

