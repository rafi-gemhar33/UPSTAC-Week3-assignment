package org.upgrad.upstac.testrequests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.consultation.*;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.models.Gender;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class ConsultationControllerTest {


    @InjectMocks
    ConsultationController consultationController;


    @Mock
    UserLoggedInService userLoggedInService;

    @Mock
    TestRequestUpdateService testRequestUpdateService;

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_update_the_request_status(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_COMPLETED);
        testRequest.setStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);

        User user = createUser();
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        Mockito.when(testRequestUpdateService.assignForConsultation(testRequest.getRequestId(), user)).thenReturn(testRequest);

        TestRequest testRequestUpdate = consultationController.assignForConsultation(testRequest.getRequestId());

        assertThat(testRequest.getRequestId(), equalTo(testRequestUpdate.getRequestId()));
        assertThat(RequestStatus.DIAGNOSIS_IN_PROCESS, equalTo(testRequestUpdate.getStatus()));
        assertNotNull(testRequestUpdate.getConsultation());
    }

    public TestRequest getTestRequestByStatus(RequestStatus status) {
        CreateTestRequest createTestRequest = createTestRequest();
        return getMockedResponseFrom(createTestRequest, status);
    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_throw_exception(){

        Long InvalidRequestId= -34L;

        User user = createUser();
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        Mockito.when(testRequestUpdateService.assignForConsultation(InvalidRequestId, user)).thenThrow(new AppException("Invalid ID"));

        ResponseStatusException responseStatusException =
                assertThrows(ResponseStatusException.class, () -> consultationController.assignForConsultation(InvalidRequestId));

        assertThat(responseStatusException.getMessage(), containsString("Invalid ID"));
    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_valid_test_request_id_should_update_the_request_status_and_update_consultation_details(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);
        testRequest.setStatus(RequestStatus.COMPLETED);

        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest();
        User user = createUser();
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        Mockito.when(testRequestUpdateService.updateConsultation(testRequest.getRequestId(), createConsultationRequest, user)).thenReturn(testRequest);

        TestRequest updatedTestRequest = consultationController.updateConsultation(testRequest.getRequestId(), createConsultationRequest);

        assertThat(testRequest.getRequestId(), equalTo(updatedTestRequest.getRequestId()));
        assertThat(RequestStatus.COMPLETED, equalTo(updatedTestRequest.getStatus()));
        assertNotNull(updatedTestRequest.getConsultation());
    }


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_test_request_id_should_throw_exception(){
        Long InvalidRequestId = -2L;

        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest();
        User user = createUser();
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        Mockito.when(testRequestUpdateService.updateConsultation(InvalidRequestId, createConsultationRequest, user)).thenThrow(new AppException("ConstraintViolationException"));

        ResponseStatusException responseStatusException =
                assertThrows(ResponseStatusException.class, () -> consultationController.updateConsultation(InvalidRequestId, createConsultationRequest));

        assertThat(responseStatusException.getMessage(), containsString("ConstraintViolationException"));
    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_empty_status_should_throw_exception(){
        Long InvalidRequestId = -2L;

        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest();
        createConsultationRequest.setSuggestion(null);
        User user = createUser();
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        Mockito.when(testRequestUpdateService.updateConsultation(InvalidRequestId, createConsultationRequest, user)).thenThrow(new AppException("Invalid Suggestion"));

        ResponseStatusException responseStatusException =
                assertThrows(ResponseStatusException.class, () -> consultationController.updateConsultation(InvalidRequestId, createConsultationRequest));

        assertThat(responseStatusException.getMessage(), containsString("Invalid Suggestion"));
    }

    public CreateConsultationRequest getCreateConsultationRequest() {

        CreateConsultationRequest createConsultationRequest = new CreateConsultationRequest();
        createConsultationRequest.setComments("Test Comments");
        createConsultationRequest.setSuggestion(DoctorSuggestion.NO_ISSUES);

        return createConsultationRequest;

    }

    public CreateTestRequest createTestRequest() {
        CreateTestRequest createTestRequest = new CreateTestRequest();
        createTestRequest.setAddress("some Address");
        createTestRequest.setAge(98);
        createTestRequest.setEmail("someone" + "123456789" + "@some_domain.com");
        createTestRequest.setGender(Gender.MALE);
        createTestRequest.setName("some_user");
        createTestRequest.setPhoneNumber("123456789");
        createTestRequest.setPinCode(716768);
        return createTestRequest;
    }
    public TestRequest getMockedResponseFrom(CreateTestRequest createTestRequest) {
        TestRequest testRequest = new TestRequest();
        testRequest.setName(createTestRequest.getName());
        testRequest.setCreated(LocalDate.now());
        testRequest.setRequestId(1L);
        testRequest.setStatus(RequestStatus.INITIATED);
        testRequest.setAge(createTestRequest.getAge());
        testRequest.setEmail(createTestRequest.getEmail());
        testRequest.setPhoneNumber(createTestRequest.getPhoneNumber());
        testRequest.setPinCode(createTestRequest.getPinCode());
        testRequest.setAddress(createTestRequest.getAddress());
        testRequest.setGender(createTestRequest.getGender());
        testRequest.setCreatedBy(createUser());

        return testRequest;
    }

    public TestRequest getMockedResponseFrom(CreateTestRequest createTestRequest, RequestStatus requestStatus) {
        TestRequest testRequest = getMockedResponseFrom(createTestRequest);
        testRequest.setStatus(requestStatus);
        LabResult labResult = new LabResult();
        Consultation drConsultation = new Consultation();
        drConsultation.setComments("Test comments");
        drConsultation.setRequest(testRequest);
        drConsultation.setSuggestion(DoctorSuggestion.NO_ISSUES);
        testRequest.setConsultation(drConsultation);
        testRequest.setLabResult(labResult);
        return testRequest;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("some_user");
        return user;
    }

}