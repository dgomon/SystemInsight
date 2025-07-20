import com.dgomon.systeminsight.R

sealed class Destination(
    val route: String,
    val label: String,
    val iconResId: Int,
    val contentDescription: String
) {
    // Bottom navigation items
    object Logcat : Destination(
        route = "logcat",
        label = "Logcat",
        iconResId = R.drawable.ic_notes,
        contentDescription = "Logcat screen"
    )

    object Getprop : Destination(
        route = "getprop",
        label = "Getprop",
        iconResId = R.drawable.ic_properties,
        contentDescription = "Getprop screen"
    )

    object Dumpsys : Destination(
        route = "dumpsys",
        label = "Dumpsys",
        iconResId = R.drawable.ic_dumpsys,
        contentDescription = "Dumpsys screen"
    )

    // Non-bottom bar items
    object Settings : Destination(
        route = "settings",
        label = "Settings",
        iconResId = R.drawable.ic_settings,
        contentDescription = "Settings screen"
    )

    // Dynamic route for dumpsys details
    object DumpsysDetails : Destination(
        route = "dumpsysDetails/{id}",
        label = "Details",
        iconResId = R.drawable.ic_settings,
        contentDescription = "Details screen"
    )
}

val bottomBarDestinations = listOf(
    Destination.Logcat,
    Destination.Getprop,
    Destination.Dumpsys
)