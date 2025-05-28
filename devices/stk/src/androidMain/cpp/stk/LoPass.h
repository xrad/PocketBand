//
// Created by xrad on 18.08.24.
//

#ifndef POCKETBAND_LOPASS_H
#define POCKETBAND_LOPASS_H

#include "include/OnePole.h"

class LoPass : public stk::Filter {
public:
    LoPass() = default;

    stk::StkFrames & tick(stk::StkFrames &frames, unsigned int channel) override {
        return frames;
    }

    stk::OnePole f[2];
};

#endif //POCKETBAND_LOPASS_H
