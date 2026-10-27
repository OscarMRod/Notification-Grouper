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
    )

    fun isExcluded(pkg: String): Boolean =
        pkg in exact || prefixes.any { pkg.startsWith(it) }
}