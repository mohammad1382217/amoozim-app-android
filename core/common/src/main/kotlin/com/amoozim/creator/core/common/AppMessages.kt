package com.amoozim.creator.core.common

/**
 * User-facing fallback messages, in Persian, ported verbatim from the web client's
 * fallback strings (`fetchInstanceNew.ts`). Used when the API returns no usable
 * `message`. UI-owned strings live in the app's `strings.xml`; these belong to the
 * network layer and are intentionally kept as code constants.
 */
object AppMessages {
    const val GENERIC = "خطایی رخ داده است"
    const val NETWORK = "ارتباط با سرور برقرار نشد"
    const val TIMEOUT = "درخواست بیش از حد طول کشید"
    const val SESSION_EXPIRED = "جلسه شما منقضی شد. دوباره وارد شوید."
}
