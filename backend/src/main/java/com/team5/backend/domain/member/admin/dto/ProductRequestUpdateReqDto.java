import com.team5.backend.domain.member.admin.entity.ProductRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProductRequestUpdateReqDto {
    @NotNull
    private ProductRequestStatus status;
}
