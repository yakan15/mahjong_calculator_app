package miyazawa.mahjong.model

interface SpecialHandCandidateFactory {
    fun create(agari: Int, hidden: MutableList<Int>): HandCandidate?
}

interface HandCandidateFactory {
    fun create(agari: Int, opened: MutableList<MutableList<Int>>,
               hidden: MutableList<MutableList<Int>>, kanOpened: MutableList<Int>,
               kanHidden: MutableList<Int>): HandCandidate?
}

class SevenPairsFactory : SpecialHandCandidateFactory {
   override fun create(agari: Int, hidden: MutableList<Int>): HandCandidate? {
       if (SevenPairs.validate(agari, hidden)) {
           return SevenPairs(agari, hidden)
       }
        return null
   }
}

class KokushiFactory : SpecialHandCandidateFactory {
    override fun create(agari: Int, hidden: MutableList<Int>): HandCandidate? {
        if (Kokushi.validate(agari, hidden)) {
            return Kokushi(agari, hidden)
        }
        return null
    }
}
