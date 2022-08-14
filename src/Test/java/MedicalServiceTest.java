import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;

public class MedicalServiceTest {

    PatientInfoRepository pirMock;
    SendAlertService sendMes;
    BloodPressure normalBloodPressure = new BloodPressure(120, 80);
    HealthInfo goodHealthCase = new HealthInfo(new BigDecimal("36.6"), normalBloodPressure);
    PatientInfo patientInfo = new PatientInfo("1", "Jon", "Good", LocalDate.now(), goodHealthCase);
    MedicalService ms;

    @BeforeEach
    public void init() {
        pirMock = Mockito.mock(PatientInfoRepository.class);
        sendMes = Mockito.mock(SendAlertService.class);
        Mockito.when(pirMock.getById("1"))
                .thenReturn(patientInfo);
        ms = new MedicalServiceImpl(pirMock, sendMes);
    }

    @AfterEach
    public void clear() {
        pirMock = null;
        sendMes = null;
        ms = null;
    }

    @Test
    public void checkIfOkTest() {
        //arrange
        BigDecimal currentTemperature = new BigDecimal("36.6");
        //act
        ms.checkBloodPressure("1", normalBloodPressure);
        ms.checkTemperature("1", currentTemperature);
        //assert
        Mockito.verify(sendMes, Mockito.times(0)).send(anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"130,80", "140,60", "180,80", "80,120", "119,81"})
    public void checkBloodPressurePTest(String strings) {
        //arrange
        String[] str = strings.split(",");
        int high = Integer.parseInt(str[0]);
        int low = Integer.parseInt(str[1]);
        BloodPressure bp = new BloodPressure(high, low);
        //act
        ms.checkBloodPressure("1", bp);
        //assert
        Mockito.verify(sendMes, Mockito.times(1)).send(anyString());
    }

    @ParameterizedTest
    @ValueSource(doubles = {32.1, 34.5, 35.0, 37.5, 38.1, 39.3, 40.2})
    public void checkTemperaturePTest(double doubles) {
        //arrange
        BigDecimal currentTemperature = new BigDecimal(doubles);
        //act
        ms.checkTemperature("1", currentTemperature);
        //assert
        Mockito.verify(sendMes, Mockito.times(1)).send(anyString());
    }
}
