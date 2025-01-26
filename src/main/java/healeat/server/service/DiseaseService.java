package healeat.server.service;

import healeat.server.domain.Disease;
import healeat.server.repository.DiseaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;

/*    @Transactional
    public void saveDiseasesFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if(!line.isEmpty()) {
                    Disease disease = Disease.builder().name(line).build();
                    diseaseRepository.save(disease);
                }
            }
            System.out.println("CSV 데이터가 성공적으로 저장되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("CSV 파일을 읽는 중 오류 발생: \" + e.getMessage()");
        }
    }*/
}
