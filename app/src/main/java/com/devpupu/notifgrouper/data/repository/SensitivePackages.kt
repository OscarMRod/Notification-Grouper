package com.devpupu.notifgrouper.data.repository

object SensitivePackages {
    val exact: Set<String> = setOf(
        "com.google.android.apps.authenticator2",
        "com.authy.authy",
        "com.lastpass.lpandroid",
        "com.keepersecurity.android",
        "com.bitwarden.mobile",
        "com.agilebits.onepassword",
        "com.dashlane",
        // messaging
        "com.whatsapp",
        "com.facebook.orca",
        "org.telegram.messenger",
        "org.thoughtcrime.securesms",
        "com.viber.voip",
        "com.snapchat.android",
        "com.discord",
        "com.skype.raider",
        "com.linecorp.line",
        "com.kakao.talk",
        // email
        "com.google.android.gm",
        "com.microsoft.office.outlook",
        "com.yahoo.mobile.client.android.mail",
        "com.samsung.android.email.provider",
        "com.android.email",
        "com.mail.mobile.android.mail",
        // health
        "com.google.android.apps.fitness",
        "com.fitbit.FitbitMobile",
        "com.myfitnesspal.android",
        "com.samsung.android.app.health",
        "com.strava",
        "com.nike.plusgps",
        "com.apple.android.music",
    )

    val prefixes: List<String> = listOf(
        "com.chase",
        "com.bankofamerica",
        "com.wellsfargo",
        "com.citibank",
        "com.capitalone",
        "com.americanexpress",
        "com.paypal",
        "com.venmo",
        "com.usbank",
        "com.tdbank",
        "com.pnc",
        "com.barclays",
        "com.hsbc",
        "com.schwab",
        "com.fidelity",
        "com.vanguard",
        "com.robinhood",
        "com.coinbase",
        "com.revolut",
        "com.monzo",
        "com.starlingbank",
        "com.n26",
        "com.chime",
        "com.discover",
        "com.synchrony",
        "com.navyfederal",
        "com.usaa",
        // messaging
        "com.whatsapp",
        "com.telegram",
        "com.signal",
        "com.viber",
        "com.snapchat",
        "com.discord",
        "com.skype",
        "com.linecorp",
        "com.kakao",
        // email
        "com.google.android.gm",
        "com.microsoft.office.outlook",
        "com.yahoo.mobile.client.android.mail",
        // health
        "com.google.android.apps.fitness",
        "com.fitbit",
        "com.myfitnesspal",
        "com.samsung.android.app.health",
        "com.strava",
        "com.nike",
    )

    fun isExcluded(pkg: String): Boolean =
        pkg in exact || prefixes.any { pkg.startsWith(it) }
}