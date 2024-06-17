package cent.wong.compedia.app.ai;

import cent.wong.compedia.entity.Persona;
import cent.wong.compedia.entity.PersonaEnum;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface AIService {

    @SystemMessage("""
    Analyze persona of String based JSON that I will give to you.
    There are 5 field of question and answer that you should read and then
    go define the persona from the given data
    1. Field motivation - Question: Apa yang menjadi motivasi anda dalam mengikuti lomba?
    2. Field problemWantToSolved - Question: Tipe masalah seperti apa yang biasa anda selesaikan?
    3. Field competitionExperience - Question: Berapa banyak lomba yang pernah anda ikuti dalam 1 semester terakhir?
    4. Field totalWinCompetition - Question: Berapa banyak lomba yang pernah anda menangkan selama menjadi mahasiswa?
    5. Field nameOfWinningCompetition - Question: List kejuaraan-kejuaraan dalam lomba yang pernah kamu raih selama kuliah?
    """)
    PersonaEnum anaylzePersona(String message);
}
