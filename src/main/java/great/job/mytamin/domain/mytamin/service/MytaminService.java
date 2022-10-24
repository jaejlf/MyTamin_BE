package great.job.mytamin.domain.mytamin.service;

import great.job.mytamin.domain.mytamin.dto.response.*;
import great.job.mytamin.domain.mytamin.entity.Care;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.entity.Report;
import great.job.mytamin.domain.mytamin.repository.MytaminRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.util.ReportUtil;
import great.job.mytamin.domain.util.TimeUtil;
import great.job.mytamin.global.exception.MytaminException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static great.job.mytamin.global.exception.ErrorMap.MYTAMIN_NOT_FOUND_ERROR;
import static great.job.mytamin.global.exception.ErrorMap.NO_AUTH_ERROR;

@Service
@RequiredArgsConstructor
public class MytaminService {

    private final TimeUtil timeUtil;
    private final ReportUtil reportUtil;
    private final MytaminRepository mytaminRepository;

    /*
    숨 고르기 완료
    */
    @Transactional
    public void completeBreath(User user) {
        user.updateBreathTime();
    }

    /*
    감각 깨우기 완료
    */
    @Transactional
    public void completeSense(User user) {
        user.updateSenseTime();
    }

    /*
    최근 섭취한 마이타민
    */
    @Transactional(readOnly = true)
    public MytaminResponse getLatestMytamin(User user) {
        Mytamin mytamin = mytaminRepository.findFirstByUserOrderByMytaminIdDesc(user);
        if (mytamin == null) return null;
        return MytaminResponse.of(
                mytamin,
                getReportResponse(mytamin),
                getCareResponse(mytamin));
    }

    /*
    마이타민 가져오기 + 없다면 생성
    */
    @Transactional
    public Mytamin findMytaminOrNew(User user) {
        Mytamin mytamin = findMytamin(user, LocalDateTime.now());
        if (mytamin == null) mytamin = createMytamin(user);
        return mytamin;
    }

    /*
    마이타민 가져오기
    */
    @Transactional(readOnly = true)
    public Mytamin findMytamin(User user, LocalDateTime target) {
        LocalDateTime takeAt = timeUtil.convertToMytaminDate(target);
        return mytaminRepository.findByUserAndTakeAt(user, takeAt)
                .orElse(null);
    }

    /*
    마이타민 생성
    */
    @Transactional
    public Mytamin createMytamin(User user) {
        Mytamin mytamin = new Mytamin(timeUtil.convertToMytaminDate(LocalDateTime.now()), user);
        return mytaminRepository.save(mytamin);
    }

    /*
    월간 마이타민 조회
    */
    @Transactional(readOnly = true)
    public List<MonthlyMytaminResponse> getMonthlyMytamin(User user, String date) {
        LocalDateTime target = timeUtil.convertRawToLocalDateTime(date);
        List<Mytamin> mytaminList = getMonthlyMytaminList(user, target);

        List<MonthlyMytaminResponse> monthlyMytaminResponseList = initList(target);
        for (Mytamin mytamin : mytaminList) {
            int index = mytamin.getTakeAt().getDayOfMonth() - 1;
            MonthlyMytaminResponse tmp = monthlyMytaminResponseList.get(index);
            monthlyMytaminResponseList.set(
                    index,
                    MonthlyMytaminResponse.builder()
                            .day(tmp.getDay())
                            .mentalConditionCode(mytamin.getReport() == null ? 9 : mytamin.getReport().getMentalConditionCode()) // 칭찬 처방만 존재하는 경우 -> 9
                            .build()
            );
        }

        return monthlyMytaminResponseList;
    }

    /*
    주간 마이타민 조회
    */
    @Transactional(readOnly = true)
    public Map<Integer, WeeklyMytaminResponse> getWeeklyMytamin(User user, String date) {
        LocalDateTime target = timeUtil.convertRawDDToLocalDateTime(date);
        LocalDateTime monday = target.minusDays(target.getDayOfWeek().getValue() - 1);

        Map<Integer, WeeklyMytaminResponse> map = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            target = monday.plusDays(i);
            Mytamin mytamin = findMytamin(user, target);
            if (mytamin != null) {
                map.put(target.getDayOfMonth(),
                        WeeklyMytaminResponse.of(
                                mytamin,
                                getReportResponse(mytamin),
                                getCareResponse(mytamin)
                        ));
            } else {
                map.put(target.getDayOfMonth(), null);
            }
        }
        return map;
    }

    /*
    마이타민 삭제
    */
    @Transactional
    public void deleteMytamin(User user, Long mytaminId) {
        Mytamin mytamin = findMytaminById(mytaminId);
        hasAuthorized(mytamin, user);
        mytaminRepository.delete(mytamin);
    }

    private List<Mytamin> getMonthlyMytaminList(User user, LocalDateTime target) {
        LocalDateTime start = LocalDateTime.of(target.getYear(), target.getMonth().getValue(), 1, 0, 0);
        LocalDateTime end = timeUtil.getLastDayOfMonth(target);
        return mytaminRepository.findAllByUserAndTakeAtBetween(user, start, end);
    }

    private List<MonthlyMytaminResponse> initList(LocalDateTime target) {
        int lastDay = timeUtil.getLastDayOfMonth(target).getDayOfMonth();
        List<MonthlyMytaminResponse> monthlyMytaminResponseList = new ArrayList<>();
        for (int i = 1; i <= lastDay; i++) {
            monthlyMytaminResponseList.add(
                    MonthlyMytaminResponse.builder()
                            .day(i)
                            .mentalConditionCode(0)
                            .build()
            );
        }
        return monthlyMytaminResponseList;
    }

    private ReportResponse getReportResponse(Mytamin mytamin) {
        Report report = mytamin.getReport();
        ReportResponse reportResponse = null;
        if (report != null) {
            reportResponse = ReportResponse.of(
                    report,
                    reportUtil.concatFeelingTag(report),
                    timeUtil.canEditReport(report));
        }
        return reportResponse;
    }

    private CareResponse getCareResponse(Mytamin mytamin) {
        Care care = mytamin.getCare();
        CareResponse careResponse = null;
        if (care != null) {
            careResponse = CareResponse.of(care, timeUtil.canEditCare(care));
        }
        return careResponse;
    }

    private void hasAuthorized(Mytamin mytamin, User user) {
        if (!mytamin.getUser().equals(user)) {
            throw new MytaminException(NO_AUTH_ERROR);
        }
    }

    private Mytamin findMytaminById(Long mytaminId) {
        return mytaminRepository.findById(mytaminId)
                .orElseThrow(() -> new MytaminException(MYTAMIN_NOT_FOUND_ERROR));
    }

}
