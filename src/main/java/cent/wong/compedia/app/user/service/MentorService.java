package cent.wong.compedia.app.user.service;

import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.MentorData;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.User;
import cent.wong.compedia.entity.dto.mentor.GetMentorDataReq;
import cent.wong.compedia.entity.dto.mentor.GetMentorDetailRes;
import cent.wong.compedia.entity.dto.mentor.GetMentorReq;
import cent.wong.compedia.entity.dto.mentor.GetMentorRes;
import cent.wong.compedia.entity.dto.user.*;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public interface MentorService {
    Mono<BaseResponse<MentorData>> save(Authentication authentication, SaveMentorDataReq mentorData);
    Mono<BaseResponse<GetMentorDataReq>> get(Authentication authentication);
    Mono<BaseResponse<GetMentorDetailRes>> getMentorDetail(GetUserReq getReq);
    Mono<BaseResponse<PaginationRes<GetMentorRes>>> getList(Authentication authentication);
    Mono<BaseResponse<Object>> updateApproval(Authentication authentication, MentorUpdateApprovalReq req);
    Mono<BaseResponse<Object>> updateMentorDetail(Authentication authentication, MentorUpdateDetailReq req);
}
