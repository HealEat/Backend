package healeat.server.service.search;

import lombok.Builder;

import java.util.Objects;
import java.util.UUID;

@Builder
public record SearchResultKey(
        String query,
        String x,
        String y
) {
    private static final double LOCATION_THRESHOLD = 0.02; // 약 200m 정도의 차이

    public String generateId() {
        // x, y 좌표를 반올림하여 비슷한 위치는 동일하게 처리
        String roundedX = roundCoordinate(x);
        String roundedY = roundCoordinate(y);

        return String.valueOf(Objects.hash(query, roundedX, roundedY));
    }

    private String roundCoordinate(String coordinate) {
        if (coordinate == null || coordinate.isBlank()) {
            return "0.0";  // 또는 다른 기본값
        }

        try {
            double value = Double.parseDouble(coordinate);
            return String.valueOf(Math.round(value / LOCATION_THRESHOLD) * LOCATION_THRESHOLD);
        } catch (NumberFormatException e) {
            return "0.0";  // 파싱 실패시 기본값
        }
    }
}