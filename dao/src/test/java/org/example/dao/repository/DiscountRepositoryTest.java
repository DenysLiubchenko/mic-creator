package org.example.dao.repository;

import org.example.dao.ModelUtils;
import org.example.dao.adapters.DiscountJpaAdapter;
import org.example.dao.entity.DiscountEntity;
import org.example.dao.mapper.DiscountEntityMapper;
import org.example.domain.dto.DiscountDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class DiscountRepositoryTest {
    @Mock
    private DiscountEntityMapper discountEntityMapper;

    @Mock
    private DiscountJpaAdapter discountJpaAdapter;

    @InjectMocks
    private DiscountRepositoryImpl discountRepository;

    private final DiscountDto discountDto = ModelUtils.getDiscountDto();
    private final DiscountEntity discountEntity = ModelUtils.getDiscountEntity();

    @Test
    void saveDiscountTest() {
        // Given
        given(discountEntityMapper.fromDto(discountDto)).willReturn(discountEntity);
        given(discountJpaAdapter.save(discountEntity)).willReturn(discountEntity);
        given(discountEntityMapper.toDto(discountEntity)).willReturn(discountDto);

        // When
        DiscountDto result = discountRepository.save(discountDto);

        // Then
        then(discountEntityMapper).should().fromDto(discountDto);
        then(discountJpaAdapter).should().save(discountEntity);
        then(discountJpaAdapter).should().flush();
        then(discountEntityMapper).should().toDto(discountEntity);
        assertThat(result).isEqualTo(discountDto);
    }

    @Test
    void saveDiscount_whenSaveFails_throwsRuntimeExceptionTest() {
        // Given
        given(discountEntityMapper.fromDto(discountDto)).willReturn(discountEntity);
        given(discountJpaAdapter.save(discountEntity)).willThrow(new RuntimeException("Save failed"));

        // When
        assertThatThrownBy(() -> discountRepository.save(discountDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Save failed");

        // Then
        then(discountEntityMapper).should().fromDto(discountDto);
        then(discountJpaAdapter).should().save(discountEntity);
    }
}
