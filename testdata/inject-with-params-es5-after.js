describe('My suite', function () {

  let theOne, someService;

  beforeEach(inject(function (one, _someService_) {
    theOne = one;
    someService = _someService_;
  }));
});
