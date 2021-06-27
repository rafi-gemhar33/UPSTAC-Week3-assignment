package org.upgrad.upstac.testrequests.lab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;


@RestController
@RequestMapping("/api/labrequests")
public class LabRequestController {

    @Autowired
    private TestRequestUpdateService requestsUpdateService;

    @Autowired
    private TestRequestQueryService testRequestQueryService;

    @Autowired
    private UserLoggedInService userLoggedInService;

    @Autowired
    LabResultService labResultService;



    @GetMapping("/to-be-tested")
    @PreAuthorize("hasAnyRole('TESTER')")
    public List<TestRequest> getForTests()  {

        try {
            return testRequestQueryService.findBy(RequestStatus.INITIATED);
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TESTER')")
    public List<TestRequest> getForTester()  {

        try {
            User loggedInUser = userLoggedInService.getLoggedInUser();
            return testRequestQueryService.findByTester(loggedInUser);
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('TESTER')")
    @PutMapping("/assign/{id}")
    public TestRequest assignForLabTest(@PathVariable Long id) {

        try {
            User loggedInUser = userLoggedInService.getLoggedInUser();
            return requestsUpdateService.assignForLabTest(id, loggedInUser);
        }catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('TESTER')")
    @PutMapping("/update/{id}")
    public TestRequest updateLabTest(@PathVariable Long id,@RequestBody CreateLabResult createLabResult) {

        try {
            User loggedInUser = userLoggedInService.getLoggedInUser();
            return requestsUpdateService.updateLabTest(id, createLabResult, loggedInUser);
        } catch (ConstraintViolationException e) {
            throw asConstraintViolation(e);
        }catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }
}