package com.example.ui.viewmodel

data class JawExerciseStage(
    val stageNumber: Int,
    val title: String,
    val subtitle: String,
    val durationSeconds: Int,
    val soundCue: String,
    val ttsGuideScript: String,
    val steps: List<String>,
    val clinicalBenefit: String
)

data class KakaktuaLyricLine(
    val id: Int,
    val leadInLyric: String,
    val leadInTts: String,
    val targetSyllable1: String,
    val targetSyllable2: String,
    val targetExtraSyllable: String? = null,
    val jawCoachingTip: String,
    val isChorusGym: Boolean = false
)

data class ParentGoldenRule(
    val id: Int,
    val dontTitle: String,
    val dontExample: String,
    val doTitle: String,
    val doExample: String,
    val clinicalReason: String
)

object ClinicalContentProvider {

    val jawExerciseStages = listOf(
        JawExerciseStage(
            stageNumber = 1,
            title = "Tahap 1: Pijat Sendi Rahang (TMJ) & Pipi",
            subtitle = "Melemaskan Otot Masseter & Sendi Depan Telinga",
            durationSeconds = 120,
            soundCue = "Mmmm... Aaaa...",
            ttsGuideScript = "Tahap satu. Pijat lembut memutar dari depan lubang telinga turun ke pipi dan dagu. Sambil bersenandung, Mmmm, Aaaa.",
            steps = listOf(
                "Hangatkan ujung jari telunjuk & jari tengah Abi.",
                "Letakkan tepat di depan lubang telinga Adik (sendi TMJ) lalu pijat memutar lembut turun ke pipi & sudut rahang.",
                "Ajak Adik bersenandung 'Mmmm...' lalu buka mulut lebar 'Aaaa...' mengikuti usapan jari ke dagu."
            ),
            clinicalBenefit = "Mengurangi kekakuan sendi rahang sehingga Adik tidak lagi menahan mulut di awal kata."
        ),
        JawExerciseStage(
            stageNumber = 2,
            title = "Tahap 2: Ketukan Bibir (Lip Tapping)",
            subtitle = "Aktivasi Sensorik Otot Lingkar Bibir (Orbicularis Oris)",
            durationSeconds = 90,
            soundCue = "Tik-tik-tik... HAAP!",
            ttsGuideScript = "Tahap dua. Ketuk ringan melingkari bibir atas dan bibir bawah. Tik, tik, tik, Haap! Baa!",
            steps = listOf(
                "Gunakan ujung jari telunjuk untuk mengetuk ringan melingkari bibir atas dan bawah Adik.",
                "Ucapkan ritmis: 'Tik-tik-tik-tik... HAAP!' sambil membuka dan menutup bibir.",
                "Lanjutkan dengan permainan cium jauh 'Muuu-AAAH!' untuk melatih transisi bibir maju ke rahang terbuka."
            ),
            clinicalBenefit = "Membangun kesadaran konsonan bilabial (M, B, P) agar suku kata depan seperti MA, BU, dan MO muncul utuh."
        ),
        JawExerciseStage(
            stageNumber = 3,
            title = "Tahap 3: Auman Singa & Vokal Terbuka (A - O - U)",
            subtitle = "Ekstensi Rahang Vertikal 100% & Permainan Pipi Gembung",
            durationSeconds = 120,
            soundCue = "HAAAAH! AAAUUUM! PUFF!",
            ttsGuideScript = "Tahap tiga. Buka mulut selebar singa mengaum! Haaah! Aaauuum! Lalu gembungkan pipi dan tepuk, Puff!",
            steps = listOf(
                "Tiru gaya Singa Menguap: turunkan rahang bawah 100% ke bawah sambil bersuara 'HAAAAH! AAAUUUM!'",
                "Latih urutan vokal lambat dengan gerakan ekstrem: 'AAAA' (lebar) -> 'OOOO' (bulat) -> 'UUUU' (maju).",
                "Gembungkan kedua pipi penuh udara, tahan 2 detik, lalu tepuk pelan bersama Adik: 'PUFF!'"
            ),
            clinicalBenefit = "Melatih jangkauan gerak rahang vertikal maksimal dan tekanan udara intra-oral."
        ),
        JawExerciseStage(
            stageNumber = 4,
            title = "Tahap 4: Teknik Jaw Prompting (Bantuan Jari Abi)",
            subtitle = "Panduan Taktil Bawah Dagu untuk Transisi 2 Suku Kata",
            durationSeconds = 120,
            soundCue = "A-BI ... MA-KAN!",
            ttsGuideScript = "Tahap empat. Letakkan jari telunjuk Abi di bawah dagu Adik. Bantu turunkan dagu saat berpindah dari suku kata pertama ke suku kata kedua. A, bi, Ma, kan.",
            steps = listOf(
                "Letakkan sisi jari telunjuk Abi dengan sangat lembut tepat di bawah tulang dagu Adik.",
                "Saat mengucapkan suku kata pertama ('MA' pada Makan atau 'BU' pada Buka), beri dorongan lembut ke bawah agar dagu turun.",
                "Ucapkan jangkar 'A-BI' dulu (dagu sudah terbuka di A), lalu langsung pandu dagu turun di 'MA-KAN'."
            ),
            clinicalBenefit = "Memutus kebiasaan Initial Syllable Deletion ('..kan', '..ka') dengan bantuan proprioseptif nyata."
        )
    )

    val kakaktuaLyrics = listOf(
        KakaktuaLyricLine(
            id = 1,
            leadInLyric = "Bu-rung ka-kak...",
            leadInTts = "Bu rung, ka kak",
            targetSyllable1 = "TU",
            targetSyllable2 = "A",
            jawCoachingTip = "Bibir maju bulat di 'TU', lalu BUKA RAHANG LEBAR di 'A'!",
            isChorusGym = false
        ),
        KakaktuaLyricLine(
            id = 2,
            leadInLyric = "Hing-gap di jen-...",
            leadInTts = "Hing gap, di jen",
            targetSyllable1 = "DE",
            targetSyllable2 = "LA",
            jawCoachingTip = "Tarik senyum ringan di 'DE', lalu turunkan rahang lebar di 'LA'!",
            isChorusGym = false
        ),
        KakaktuaLyricLine(
            id = 3,
            leadInLyric = "Ne-nek su-dah...",
            leadInTts = "Ne nek, su dah",
            targetSyllable1 = "TU",
            targetSyllable2 = "A",
            jawCoachingTip = "Jeda 3 detik! Biarkan Adik yang berteriak gembira 'TU - AAA!'",
            isChorusGym = false
        ),
        KakaktuaLyricLine(
            id = 4,
            leadInLyric = "Gi-gi-nya ting-gal...",
            leadInTts = "Gi gi nya, ting gal",
            targetSyllable1 = "DU",
            targetSyllable2 = "A",
            jawCoachingTip = "Sentuh ujung lidah di 'DU', lalu buka rahang lebar 100% di 'A'!",
            isChorusGym = false
        ),
        KakaktuaLyricLine(
            id = 5,
            leadInLyric = "Reff Senam Rahang & Ritme Paha:",
            leadInTts = "Ayo tepuk paha kiri dan kanan, Tek dung, La la la!",
            targetSyllable1 = "TEK",
            targetSyllable2 = "DUNG",
            targetExtraSyllable = "LA - LA - LA",
            jawCoachingTip = "Tepuk Paha Kiri-Kanan saat 'TEK-DUNG', lalu tahan rahang terbuka lebar sambil lidah naik-turun 'LA-LA-LA'!",
            isChorusGym = true
        )
    )

    val parentGoldenRules = listOf(
        ParentGoldenRule(
            id = 1,
            dontTitle = "JANGAN Menodong: \"Ayo bilang dulu!\"",
            dontExample = "\"Ayo bilang 'Makan' dulu baru Abi suapin! Ulangi: Ma-kan!\"",
            doTitle = "LAKUKAN: Pancing dengan Kata Jangkar & Ketukan",
            doExample = "\"Wah, A-bi MI-num... Sekarang A-bi MA-kan! MA... KAN!\"",
            clinicalReason = "Menodong memicu ketegangan otot rahang (masseter mengunci). Bermain ritme membuat rahang turun secara alami."
        ),
        ParentGoldenRule(
            id = 2,
            dontTitle = "JANGAN Menahan Benda Terlalu Lama",
            dontExample = "Menyembunyikan mainan/susu sampai anak menangis atau frustrasi.",
            doTitle = "LAKUKAN: Aturan 3-5 Detik Sejajar Mulut Abi",
            doExample = "Pegang benda tepat di samping bibir Abi selama 3-5 detik agar mata Adik fokus ke bukaan rahang Abi, lalu berikan sambil ucapkan suku kata awalnya dengan tegas.",
            clinicalReason = "Fokus visual anak usia 3 tahun bertahan 3-5 detik. Menjajarkan benda dengan mulut Abi membantu imitasi motorik visual."
        ),
        ParentGoldenRule(
            id = 3,
            dontTitle = "JANGAN Menyalahkan Ucapan Ujung Kata",
            dontExample = "\"Kok '..kan' terus sih? Salah, yang bener MA-KAN!\"",
            doTitle = "LAKUKAN: Teknik Recasting (Pantulan Positif)",
            doExample = "Saat Adik bilang \"..ka\", langsung sambut antusias: \"Iya pintar! BU-KA! A-bi BU-KA toplesnya ya!\"",
            clinicalReason = "Recasting memvalidasi usaha komunikasi Adik sambil menanamkan jejak auditori suku kata pertama yang benar."
        )
    )
}
