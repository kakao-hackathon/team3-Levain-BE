package com.service.levain.domain.service;

import com.service.levain.domain.dto.icon.request.PurchaseIconReqDto;
import com.service.levain.domain.entity.Icon;
import com.service.levain.domain.entity.Member;
import com.service.levain.domain.enums.Icons;
import com.service.levain.domain.repository.IconRepository;
import com.service.levain.domain.repository.MemberRepository;
import com.service.levain.domain.utils.Utils;
import com.service.levain.global.common.ResponseUtils;
import com.service.levain.global.exception.CustomException;
import com.service.levain.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IconService {

    private final IconRepository iconRepository;

    private final MemberRepository memberRepository;

    public ResponseEntity<?> purchaseIcon(PurchaseIconReqDto purchaseIconReqDto) {
        // TODO: 회원에 임의 데이터를 넣었지만 후에 바꿔줘야함
        Member member = memberRepository.findById("ellie_kim")
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER));

        // 아이콘을 구매할 수 있는지 확인
        int iconNum = purchaseIconReqDto.getIconNum();
        int iconPrice = Icons.getPriceByNum(iconNum);
        if (!Utils.isPossiblePurchase(member.getReward(), iconPrice)) {
            throw new CustomException(ErrorCode.INSUFFICIENT_REWARDS);
        }

        Icon icon = Icon.purchaseOf(purchaseIconReqDto, member);
        iconRepository.save(icon);

        member.updateReward(member.getReward() - iconPrice);
        memberRepository.save(member);

        return ResponseUtils.createResponse(HttpStatus.OK, "아이콘 구매 성공");
    }
}
