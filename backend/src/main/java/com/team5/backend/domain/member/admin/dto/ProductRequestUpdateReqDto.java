import com.team5.backend.domain.member.admin.entity.ProductRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRequestUpdateReqDto {
    @NotNull
    private ProductRequestStatus status;
}
