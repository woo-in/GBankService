package bankapp.member.service.check;

import bankapp.member.dao.MemberDao;
import bankapp.member.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultCheckServiceTest {

    @Mock
    private MemberDao memberDao;

    @InjectMocks
    private DefaultCheckService defaultCheckService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setMemberId(1L);
        testMember.setUsername("testuser");
        testMember.setPassword("password");
        testMember.setName("테스트 사용자");
    }

    @Test
    void isMemberIdExist_존재하는_회원ID_테스트() {
        // given
        Long existingMemberId = 1L;
        when(memberDao.findByMemberId(existingMemberId)).thenReturn(testMember);

        // when
        boolean result = defaultCheckService.isMemberIdExist(existingMemberId);

        // then
        assertTrue(result);
        verify(memberDao, times(1)).findByMemberId(existingMemberId);
    }

    @Test
    void isMemberIdExist_존재하지않는_회원ID_테스트() {
        // given
        Long nonExistingMemberId = 999L;
        when(memberDao.findByMemberId(nonExistingMemberId)).thenReturn(null);

        // when
        boolean result = defaultCheckService.isMemberIdExist(nonExistingMemberId);

        // then
        assertFalse(result);
        verify(memberDao, times(1)).findByMemberId(nonExistingMemberId);
    }

    @Test
    void isMemberIdExist_null값_테스트() {
        // given
        Long nullMemberId = null;
        when(memberDao.findByMemberId(nullMemberId)).thenReturn(null);

        // when
        boolean result = defaultCheckService.isMemberIdExist(nullMemberId);

        // then
        assertFalse(result);
        verify(memberDao, times(1)).findByMemberId(nullMemberId);
    }

    @Test
    void isUsernameExist_존재하는_사용자명_테스트() {
        // given
        String existingUsername = "testuser";
        when(memberDao.findByUsername(existingUsername)).thenReturn(testMember);

        // when
        boolean result = defaultCheckService.isUsernameExist(existingUsername);

        // then
        assertTrue(result);
        verify(memberDao, times(1)).findByUsername(existingUsername);
    }

    @Test
    void isUsernameExist_존재하지않는_사용자명_테스트() {
        // given
        String nonExistingUsername = "nonexistentuser";
        when(memberDao.findByUsername(nonExistingUsername)).thenReturn(null);

        // when
        boolean result = defaultCheckService.isUsernameExist(nonExistingUsername);

        // then
        assertFalse(result);
        verify(memberDao, times(1)).findByUsername(nonExistingUsername);
    }

    @Test
    void isUsernameExist_null값_테스트() {
        // given
        String nullUsername = null;
        when(memberDao.findByUsername(nullUsername)).thenReturn(null);

        // when
        boolean result = defaultCheckService.isUsernameExist(nullUsername);

        // then
        assertFalse(result);
        verify(memberDao, times(1)).findByUsername(nullUsername);
    }

    @Test
    void isUsernameExist_빈문자열_테스트() {
        // given
        String emptyUsername = "";
        when(memberDao.findByUsername(emptyUsername)).thenReturn(null);

        // when
        boolean result = defaultCheckService.isUsernameExist(emptyUsername);

        // then
        assertFalse(result);
        verify(memberDao, times(1)).findByUsername(emptyUsername);
    }
}