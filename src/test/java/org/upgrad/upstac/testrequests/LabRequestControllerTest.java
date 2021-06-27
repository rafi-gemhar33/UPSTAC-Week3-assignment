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
import org.upgrad.upstac.testrequests.lab.*;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.models.Gender;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class LabRequestControllerTest {


    @InjectMocks
    LabRequestController labRequestController;


    @Mock
    UserLoggedInService userLoggedInService;

    @Mock
    TestRequestUpdateService testRequestUpdateService;


    @Test
    @WithUserDetails(value = "tester")
    public void calling_assignForLabTest_with_valid_test_request_id_should_update_the_request_status(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.INITIATED);
        Long validId = 1L;
        User user = createUser();
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        Mockito.when(testRequestUpdateService.assignForLabTest(validId, user)).thenReturn(testRequest);


        TestRequest testRequest2 = labRequestController.assignForLabTest(validId);

        assertThat(testRequest.getRequestId(), equalTo(testRequest2.getRequestId()));
        assertThat(RequestStatus.INITIATED, equalTo(testRequest2.getStatus()));
        assertNotNull(testRequest2.getLabResult());
    }

    public TestRequest getTestRequestByStatus(RequestStatus status) {
        CreateTestRequest createTestRequest = createTestRequest();
        return getMockedResponseFrom(createTestRequest, status);
    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_assignForLabTest_with_valid_test_request_id_should_throw_exception(){

        Long InvalidRequestId= -32L;
        User user = createUser();
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        Mockito.when(testRequestUpdateService.assignForLabTest(InvalidRequestId, user)).thenThrow(new AppException("Invalid ID"));

        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, ()-> labRequestController.assignForLabTest(InvalidRequestId));
        assertNotNull(responseStatusException);
        assertThat(responseStatusException.getMessage(), containsString("Invalid ID"));
    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_valid_test_request_id_should_update_the_request_status_and_update_test_request_details(){
        Long id = 1L;
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);

        CreateLabResult createLabResult = getCreateLabResult();
        User user = createUser();
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        Mockito.when(testRequestUpdateService.updateLabTest(id, createLabResult, user)).thenReturn(testRequest);

        TestRequest updateTestRequest = labRequestController.updateLabTest(1L, createLabResult);

        assertThat(testRequest.getRequestId(), equalTo(updateTestRequest.getRequestId()));
        assertThat(RequestStatus.LAB_TEST_IN_PROGRESS, equalTo(updateTestRequest.getStatus()));
        assertNotNull(updateTestRequest.getLabResult());
    }


    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_test_request_id_should_throw_exception(){

        Long InvalidRequestId= -3L;

        CreateLabResult createLabResult = getCreateLabResult();
        User user = createUser();
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        Mockito.when(testRequestUpdateService.updateLabTest(InvalidRequestId, createLabResult, user)).thenThrow(new AppException("Invalid ID"));

        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, ()-> labRequestController.updateLabTest(InvalidRequestId, createLabResult));
        assertNotNull(responseStatusException);
        assertThat(responseStatusException.getMessage(), containsString("Invalid ID"));
    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_empty_status_should_throw_exception(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);
        CreateLabResult createLabResult = getCreateLabResult();
        User user = createUser();
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        Mockito.when(testRequestUpdateService.updateLabTest(testRequest.getRequestId(), createLabResult, user)).thenThrow(new AppException("ConstraintViolationException"));

        ResponseStatusException result = assertThrows(ResponseStatusException.class, ()-> labRequestController.updateLabTest(testRequest.getRequestId(), createLabResult));
        assertNotNull(result);

        assertThat(result.getMessage(), containsString("ConstraintViolationException"));
    }

    public CreateLabResult getCreateLabResult() {
        CreateLabResult createLabResult = new CreateLabResult();
        createLabResult.setBloodPressure("102");
        createLabResult.setHeartBeat("88");
        createLabResult.setOxygenLevel("98");
        createLabResult.setTemperature("99");
        createLabResult.setResult(TestStatus.NEGATIVE);

        return createLabResult;
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
        labResult.setBloodPressure("102");
        labResult.setHeartBeat("88");
        labResult.setOxygenLevel("98");
        labResult.setTemperature("99");
        labResult.setResult(TestStatus.NEGATIVE);
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